package memoraize.domain.album.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import memoraize.domain.album.validation.annotation.NonNegative;
import memoraize.global.enums.statuscode.ErrorStatus;

public class NonNegativeValidator implements ConstraintValidator<NonNegative, Integer> {

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		boolean isValid = value < 0 ? false : true;

		if (!isValid) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(ErrorStatus._PAGE_NOT_NEGATIVE.getMessage().toString())
				.addConstraintViolation();
		}
		return isValid;
	}

	@Override
	public void initialize(NonNegative constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}
}
