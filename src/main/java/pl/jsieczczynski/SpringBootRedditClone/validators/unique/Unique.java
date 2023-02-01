package pl.jsieczczynski.SpringBootRedditClone.validators.unique;

import pl.jsieczczynski.SpringBootRedditClone.validators.FieldValueExists;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@Constraint(validatedBy = UniqueValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Unique {
    String message() default "{unique.value.violation}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends FieldValueExists> service();

    String serviceQualifier() default "";

    String fieldName();
}
