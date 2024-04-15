package memoraize.domain.album.web.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import memoraize.domain.album.enums.AlbumAccess;
import memoraize.domain.album.enums.SortStatus;
import memoraize.domain.album.validation.annotation.ListNotBlank;
import memoraize.domain.album.validation.annotation.NonNegative;

public class AlbumPostRequestDTO {

	@Getter
	@Setter
	public static class addAlbumPostDTO {
		private String albumName;
		private String albumInfo;
		//private User user;
		@ListNotBlank
		private List<MultipartFile> images;
		private AlbumAccess albumAccess;
		private Boolean isDeleted;
	}

	@Getter
	@Setter
	public static class getAlbumPostPageDTO {

		@NotNull(message = "정렬 기준은 필수입니다.")
		private SortStatus sortStatus;

		@NonNegative
		@NotNull(message = "page값은 필수입니다.")
		private Integer page;

		@NonNegative
		@NotNull(message = "albumCount값은 필수입니다.")
		private Integer albumCount;
	}
}
