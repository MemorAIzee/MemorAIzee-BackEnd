package memoraize.domain.album.service;

import memoraize.domain.album.web.dto.AlbumPostRequestDTO;
import memoraize.domain.album.web.dto.AlbumPostResponseDTO;

public interface AlbumPostQueryService {
	AlbumPostResponseDTO.AlbumPostPreviewResultPageDTO getAlbumPage(AlbumPostRequestDTO.getAlbumPostPageDTO request);
}

