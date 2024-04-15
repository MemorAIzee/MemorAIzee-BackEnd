package memoraize.domain.album.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import memoraize.domain.album.validation.validator.NonNegativeValidator;

@Documented
@Constraint(validatedBy = NonNegativeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NonNegative {

	String message() default ("음수일 수 없습니다.");

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
