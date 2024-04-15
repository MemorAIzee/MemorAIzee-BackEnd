package memoraize.domain.photo.exception;

import memoraize.global.enums.statuscode.BaseCode;
import memoraize.global.exception.GeneralException;

public class ExtractPlaceException extends GeneralException {
	public ExtractPlaceException(BaseCode errorStatus) {
		super(errorStatus);
	}
}
