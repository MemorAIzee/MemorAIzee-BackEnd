package memoraize.domain.review.exception;

import memoraize.global.enums.statuscode.BaseCode;
import memoraize.global.exception.GeneralException;

public class PlaceFetchException extends GeneralException {
	public PlaceFetchException(BaseCode errorStatus) {
		super(errorStatus);
	}
}
