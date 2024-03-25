package memoraize.global.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import memoraize.global.enums.statuscode.BaseCode;
import memoraize.global.enums.statuscode.SuccessStatus;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

	private final Boolean isSuccess;
	private final String code;

	private final String message;
	private T result;

	// 성공한 경우 응답 생성

	public static <T> ApiResponse<T> onSuccess(T result) {
		return new ApiResponse<>(true, SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(), result);
	}

	public static <T> ApiResponse<T> of(boolean isSuccess, BaseCode code, T result) {
		return new ApiResponse<>(isSuccess, code.getCode(), code.getMessage(), result);
	}

	// 실패한 경우 응답 생성
	public static <T> ApiResponse<T> onFailure(String code, String message, T data) {
		return new ApiResponse<>(false, code, message, data);
	}
}
