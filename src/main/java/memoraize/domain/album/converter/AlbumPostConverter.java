package memoraize.domain.album.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import lombok.extern.slf4j.Slf4j;
import memoraize.domain.album.entity.Album;
import memoraize.domain.album.web.dto.AlbumPostRequestDTO;
import memoraize.domain.album.web.dto.AlbumPostResponseDTO;

@Slf4j
public class AlbumPostConverter {
	public static Album toAlbumPost(AlbumPostRequestDTO.addAlbumPostDTO request) {
		return Album.builder()
			.albumName(request.getAlbumName())
			.albumInfo(request.getAlbumInfo())
			.albumAccess(request.getAlbumAccess())
			.photoImages(new ArrayList<>())
			.isDeleted(false)
			.build();
	}

	public static AlbumPostResponseDTO.AddAlbumPostResultDTO toAddAlbumPostResultDTO(Album album) {
		return AlbumPostResponseDTO.AddAlbumPostResultDTO.builder()
			.albumId(album.getAlbumId())
			.createdAt(album.getCreatedAt())
			.build();
	}

	public static AlbumPostResponseDTO.AlbumPostPreviewResultPageDTO toAlbumPostPreviewResultPageDTO(
		Page<Album> albumPage) {
		List<AlbumPostResponseDTO.AlbumPostPreviewResultDTO> albumList = albumPage.stream()
			.map(AlbumPostConverter::toAlbumPostPreviewResultDTO)
			.collect(
				Collectors.toList());
		return AlbumPostResponseDTO.AlbumPostPreviewResultPageDTO.builder()
			.albums(albumList)
			.listSize(albumPage.getSize())
			.isFirst(albumPage.isFirst())
			.isLast(albumPage.isLast())
			.totalPage(albumPage.getTotalPages())
			.totalElements(albumPage.getTotalElements())
			.build();

	}

	public static AlbumPostResponseDTO.AlbumPostPreviewResultDTO toAlbumPostPreviewResultDTO(Album album) {
		return AlbumPostResponseDTO.AlbumPostPreviewResultDTO.builder()
			.albumId(album.getAlbumId())
			.mainImageUrl(album.getPhotoImages().get(0).getImageUrl())
			.albumName(album.getAlbumName())
			.createdAt(album.getCreatedAt())
			.build();
	}

}
