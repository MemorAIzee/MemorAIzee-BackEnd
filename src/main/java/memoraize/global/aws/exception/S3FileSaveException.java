package memoraize.global.aws.exception;

import memoraize.global.enums.statuscode.BaseCode;
import memoraize.global.exception.GeneralException;

public class S3FileSaveException extends GeneralException {
	public S3FileSaveException(BaseCode errorStatus) {
		super(errorStatus);
	}
}
