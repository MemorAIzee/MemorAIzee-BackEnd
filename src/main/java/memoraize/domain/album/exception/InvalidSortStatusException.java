package memoraize.domain.album.exception;

import memoraize.global.enums.statuscode.BaseCode;
import memoraize.global.exception.GeneralException;

public class InvalidSortStatusException extends GeneralException {
	public InvalidSortStatusException(BaseCode errorStatus) {
		super(errorStatus);
	}
}
