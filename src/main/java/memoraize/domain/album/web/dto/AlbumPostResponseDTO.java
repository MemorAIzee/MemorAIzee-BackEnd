package memoraize.domain.album.web.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AlbumPostResponseDTO {
	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AddAlbumPostResultDTO {
		private Long albumId;
		private LocalDateTime createdAt;
	}

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AlbumPostPreviewResultPageDTO {
		private List<AlbumPostPreviewResultDTO> albums;
		private Integer listSize;
		private Integer totalPage;
		private Long totalElements;
		private Boolean isFirst;
		private Boolean isLast;
	}

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AlbumPostPreviewResultDTO {
		private Long albumId;
		private String mainImageUrl;
		private String albumName;
		private LocalDateTime createdAt;

		// TODO HashTag 추가 및 앨범 URL 추가
	}

}
