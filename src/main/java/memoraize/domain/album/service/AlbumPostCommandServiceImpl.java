package memoraize.domain.album.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import memoraize.domain.album.converter.AlbumPostConverter;
import memoraize.domain.album.entity.Album;
import memoraize.domain.album.repository.AlbumPostRepository;
import memoraize.domain.album.web.dto.AlbumPostRequestDTO;
import memoraize.domain.album.web.dto.AlbumPostResponseDTO;
import memoraize.domain.photo.entity.Photo;
import memoraize.domain.photo.service.PhotoCommandService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlbumPostCommandServiceImpl implements AlbumPostCommandService {

	private final AlbumPostRepository albumPostRepository;
	private final PhotoCommandService photoCommandService;

	@Override
	@Transactional
	public AlbumPostResponseDTO.AddAlbumPostResultDTO addAlbum(AlbumPostRequestDTO.addAlbumPostDTO request) {
		Album albumPost = AlbumPostConverter.toAlbumPost(request);
		List<Photo> photoList = photoCommandService.savePhotoImages(request.getImages());
		albumPost.addPhotoImages(photoList);

		// 앨범 추가 기능

		Album savedAlbum = albumPostRepository.save(albumPost);
		return AlbumPostConverter.toAddAlbumPostResultDTO(savedAlbum);
	}

}
