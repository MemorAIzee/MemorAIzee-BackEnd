package memoraize.domain.photo.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import memoraize.domain.photo.entity.Photo;
import memoraize.domain.photo.entity.PhotoHashTag;
import memoraize.domain.photo.entity.Uuid;
import memoraize.domain.photo.enums.TagCategory;
import memoraize.domain.photo.exception.ExtractPlaceException;
import memoraize.domain.photo.repository.PhotoRepository;
import memoraize.domain.photo.repository.UuidRepository;
import memoraize.domain.review.converter.PlaceConverter;
import memoraize.domain.review.entity.Place;
import memoraize.domain.review.repository.PlaceRepository;
import memoraize.global.aws.s3.AmazonS3Manager;
import memoraize.global.enums.statuscode.ErrorStatus;
import memoraize.global.exception.GeneralException;
import memoraize.global.gcp.map.GoogleMapManager;
import memoraize.global.gcp.map.dto.GooglePlaceApiResponseDTO;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoCommandServiceImpl implements PhotoCommandService {

	private final UuidRepository uuidRepository;
	private final AmazonS3Manager amazonS3Manager;
	private final PlaceRepository placeRepository;
	private final GoogleMapManager googleMapManager;
	private final VisionApiService visionApiService;
	private final GeminiApiService geminiApiService;

	private final PhotoRepository photoRepository;

	/**
	 * 사진 저장
	 *
	 * @param request 요청 받은 사진 목록 (MultiPartFile)
	 * @return List<Photo>
	 */

	@Override
	@Transactional
	public Photo savePhotoImages(MultipartFile image) {
		byte[] imageBytes = null;

		try {
			imageBytes = image.getBytes();
		} catch (IOException e) {
			log.error("imageByte 추출 중 오류 발생 {}", e.getMessage());
			throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
		}
		byte[] myImageByte = imageBytes;

		Photo photo = Photo.builder()
			.comment("사진에 대한 내용을 작성해주세요.")
			.title("사진 제목을 작성해주세요.")
			.photoHashTagList(new ArrayList<>())
			.build();

		// 이미지 사진 S3 저장
		CompletableFuture<String> saveImageCompletableFuture = CompletableFuture.supplyAsync(
			() -> savePhotoImage(image, myImageByte));

		// 이미지 파일 위치 정보 추출
		Optional<memoraize.domain.photo.entity.Metadata> metadata = extractMetadata(image);

		// 장소 정보 추출 후 적용
		CompletableFuture<Place> placeCompletableFuture = CompletableFuture.supplyAsync(
			() -> getPlace(metadata, photo));

		// 해쉬태그 추출
		CompletableFuture<List<PhotoHashTag>> hashTagsCompletableFuture = CompletableFuture.supplyAsync(
			() -> getPhotoHashTags(image, myImageByte, photo));

		CompletableFuture<Void> allFutures = CompletableFuture.allOf(saveImageCompletableFuture,
			placeCompletableFuture, hashTagsCompletableFuture);

		allFutures.thenRun(() -> {
			log.info("모든 스레드 처리가 완료되었습니다.");
			log.info("place = {}, hashTag = {}, saveImage = {}", placeCompletableFuture.isDone(),
				hashTagsCompletableFuture.isDone(),
				saveImageCompletableFuture.isDone());
			try {
				String imageUrl = saveImageCompletableFuture.get();
				Place place = placeCompletableFuture.get();
				List<PhotoHashTag> hashTagList = hashTagsCompletableFuture.get();

				// gemini title, comment
				List<String> labels = new ArrayList<>();
				List<String> colors = new ArrayList<>();
				for (PhotoHashTag hashTag : hashTagList) {
					if (hashTag.getTagCategorie() == TagCategory.LABEL)
						labels.add(hashTag.getTagName());
					if (hashTag.getTagCategorie() == TagCategory.COLOR)
						colors.add(hashTag.getTagName());
				}

				CompletableFuture<String> titleCompletableFuture = CompletableFuture.supplyAsync(
					() -> geminiApiService.generateTitle(colors, labels,place != null ? place.getPlaceName() : null));
				CompletableFuture<String> commentCompletableFuture = CompletableFuture.supplyAsync(
					() -> geminiApiService.generateComment(colors, labels,
						place != null ? place.getPlaceName() : null));

				CompletableFuture<Void> geminiFutures = CompletableFuture.allOf(titleCompletableFuture,
					commentCompletableFuture);

				geminiFutures.thenRun(() -> {
					try {
						String title = titleCompletableFuture.get();
						String comment = commentCompletableFuture.get();
						photo.setTitle(title);
						photo.setComment(comment);
					} catch (Exception e) {
						log.error("제미나이 데이터 적용 중 오류가 발생했습니다. {}", e.getMessage());
						throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
					}
				}).join();

				photo.setPlace(place);
				photo.setImageUrl(imageUrl);
				for (PhotoHashTag hashTag : hashTagList) {
					photo.addHashTag(hashTag);
				}
			} catch (Exception e) {
				log.error("사진 저장 중 에러가 발생했습니다. {}", e.getMessage());
				throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
			}

		}).join();
		return photo;
	}

	@Transactional
	public Place getPlace(Optional<memoraize.domain.photo.entity.Metadata> metadata, Photo photo) {
		if (metadata.isPresent()) {

			memoraize.domain.photo.entity.Metadata data = metadata.get();
			photo.setMetadata(data);

			// nearby search
			Optional<GooglePlaceApiResponseDTO> ret = googleMapManager.placeSearchWithGoogleMap(
				data.getLongitude(),
				data.getLatitude());
			Optional<String> placeName;

			String googlePlaceId;
			String googlePlacePhotoUrl = null;

			if (!ret.isPresent()) {
				// 주변 검색 장소가 없는 경우
				googlePlaceId = null;
				placeName = Optional.ofNullable(null);
			} else {
				placeName = Optional.ofNullable(
					ret.get().getPlaces().get(0).getDisplayName().getText());
				googlePlaceId = ret.get().getPlaces().get(0).getId();
			}

			if (!placeName.isPresent()) {
				// reverseGeocoding
				try {
					placeName = googleMapManager.reverseGeocodingWithGoogleMap(data.getLatitude(),
						data.getLongitude());
				} catch (Exception e) {
					throw new ExtractPlaceException(ErrorStatus._INTERNAL_SERVER_ERROR);
				}
			}

			String pname = placeName.get();

			Place place;
			Optional<Place> placeOptional = placeRepository.findByPlaceName(pname);
			if (placeOptional.isPresent()) {
				place = placeOptional.get();
			} else {
				if (ret.isPresent()) {
					// 사진 저장
					String referenceName = extractPhotoReference(
						ret.get().getPlaces().get(0).getPhotos().get(0).getReferenceName());
					googlePlacePhotoUrl = googleMapManager.getPlacePhotoWithGoogleMap(referenceName);
				}

				place = PlaceConverter.toPlace(pname, googlePlaceId, googlePlacePhotoUrl);
			}
			return place;
		}
		return null;
	}

	@Transactional
	public synchronized List<PhotoHashTag> getPhotoHashTags(MultipartFile image, byte[] imageBytes, Photo photo) {

		// Google Vision API 호출
		try {
			visionApiService.connect(image, imageBytes);
		} catch (IOException e) {
			log.error("Google Vision API 호출 중 오류가 발생했습니다. {}", e);
			throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
		}

		Map<TagCategory, List<String>> resultMap = visionApiService.getResultMap();
		Set<TagCategory> categorySet = resultMap.keySet();
		List<PhotoHashTag> hashTags = new ArrayList<>();

		//해시태그 리스트 생성
		for (TagCategory category : categorySet) {
			for (String tag : resultMap.get(category)) {
				PhotoHashTag photoHashTag = PhotoHashTag.builder()
					.tagName(tag)
					.tagCategorie(category)
					.genByAI(true)
					.build();

				hashTags.add(photoHashTag);
			}
		}

		for (PhotoHashTag hashTag : hashTags) {
			photo.addHashTag(hashTag);
		}
		return hashTags;
	}

	public String savePhotoImage(MultipartFile image, byte[] imageBytes) {
		// S3에 이미지 저장
		String uuid = UUID.randomUUID().toString();
		Uuid savedUuid = uuidRepository.save(Uuid.builder().uuid(uuid).build());

		return amazonS3Manager.uploadFile(amazonS3Manager.generatePhotoImageKeyName(savedUuid), image,
			imageBytes);
	}

	public static Optional<memoraize.domain.photo.entity.Metadata> extractMetadata(MultipartFile file) {
		try {
			// MultipartFile을 File로 변환. 실제 사용 시 임시 파일을 생성하고 사용 후 삭제해야 할 수 있음.
			File imageFile = convertToFile(file);

			// 사진의 메타데이터를 읽음
			Metadata metadata = ImageMetadataReader.readMetadata(imageFile);

			// 메타데이터에서 GPS 디렉토리를 가져옴
			GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
			GeoLocation geoLocation = gpsDirectory == null ? null : gpsDirectory.getGeoLocation();

			Date date = null;

			// 메타데이터에서 촬영 시각을 가져옴
			ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
			if (directory != null) {
				date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
			}

			// 만약 파일이 존재하면 삭제
			if (imageFile != null && imageFile.exists())
				imageFile.delete();

			if (geoLocation != null && date != null) {
				memoraize.domain.photo.entity.Metadata extractedMetadata = memoraize.domain.photo.entity.Metadata.builder()
					.latitude(geoLocation.getLatitude())
					.longitude(geoLocation.getLongitude())
					.date(date)
					.build();
				return Optional.ofNullable(extractedMetadata);
			} else {
				return Optional.ofNullable(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static File convertToFile(MultipartFile multipartFile) throws IOException {
		// 파일 변환 로직 구현 => 운영체제마다 tmp 폴더에 file 생성
		File file = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
		multipartFile.transferTo(file);
		return file;
	}

	private String extractPhotoReference(String referenceName) {
		String marker = "photos/";
		int index = referenceName.indexOf(marker);
		String result;
		if (index != -1) {
			result = referenceName.substring(index + marker.length());
		} else {
			result = null;
		}

		return result;
	}
}
