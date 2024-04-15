package memoraize.domain.photo.converter;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import memoraize.domain.photo.entity.Photo;
import memoraize.domain.photo.web.dto.PhotoRequestDTO;
import memoraize.domain.photo.web.dto.PhotoResponseDTO;

public class PhotoConverter {
	public static PhotoResponseDTO.saveReviewResultDTO toSaveReviewResultDTO(Photo photo) {
		return PhotoResponseDTO.saveReviewResultDTO.builder()
			.imageId(photo.getId())
			.imageUrl(photo.getImageUrl())
			.createdAt(photo.getCreatedAt())
			.build();
	}

	public static Photo toPhoto(String imageUrl, String comment) {
		return Photo.builder()
			.comment(comment)
			.imageUrl(imageUrl)
			.build();
	}

	public static Photo toPhoto(String imageUrl) {
		return toPhoto(imageUrl, null);
	}

	public static PhotoRequestDTO.savePhotoDTO toSavePhotoDTO(List<MultipartFile> request) {
		return PhotoRequestDTO.savePhotoDTO.builder()
			.photoImages(request)
			.build();
	}
}
