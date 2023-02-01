package pl.jsieczczynski.SpringBootRedditClone.validators.exists;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import pl.jsieczczynski.SpringBootRedditClone.validators.FieldValueExists;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class ExistsValidator implements ConstraintValidator<Exists, Object> {
    private final ApplicationContext applicationContext;
    private FieldValueExists service;
    private String fieldName;

    @Override
    public void initialize(Exists unique) {
        Class<? extends FieldValueExists> clazz = unique.service();
        fieldName = unique.fieldName();
        String serviceQualifier = unique.serviceQualifier();

        if (!serviceQualifier.equals("")) {
            service = applicationContext.getBean(serviceQualifier, clazz);
        } else {
            service = applicationContext.getBean(clazz);
        }
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return service.fieldValueExists(o, fieldName);
    }
}
