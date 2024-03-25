package memoraize.global.enums.statuscode;

import org.springframework.http.HttpStatus;

public interface BaseCode {
	String getCode();

	String getMessage();

	HttpStatus getStatus();

	Integer getStatusValue();
}
