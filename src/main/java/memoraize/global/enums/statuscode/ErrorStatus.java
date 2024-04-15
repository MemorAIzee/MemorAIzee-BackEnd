package memoraize.global.enums.statuscode;

import org.springframework.http.HttpStatus;

public enum ErrorStatus implements BaseCode {
	// common
	_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
	_BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
	_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
	_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

	// Album
	_PHOTO_IMAGE_NOT_EXIST(HttpStatus.BAD_REQUEST, "ALBUM4001", "업로드된 사진이 없습니다."),
	_PAGE_NOT_NEGATIVE(HttpStatus.BAD_REQUEST, "ALBUM4002", "page번호나 pageCount는 음수일 수 없습니다."),
	_INVALID_SORT_STATUS(HttpStatus.BAD_REQUEST, "ALBUM4003", "정렬 기준이 올바르지 않습니다."),

	// Place
	_PLACE_FETCH_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PLACE5001", "장소에 중복되는 값이 있습니다."),

	//S3
	_S3_FILE_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S35001", "S3 파일 저장에 실패했습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	ErrorStatus(HttpStatus httpStatus, String code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	@Override
	public Integer getStatusValue() {
		return httpStatus.value();
	}
}
