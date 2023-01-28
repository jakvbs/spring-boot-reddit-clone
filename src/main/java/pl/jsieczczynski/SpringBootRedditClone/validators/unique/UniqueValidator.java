package pl.jsieczczynski.SpringBootRedditClone.validators.unique;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UniqueValidator implements ConstraintValidator<Unique, Object> {
    private final ApplicationContext applicationContext;
    private FieldValueExists service;
    private String fieldName;

    @Override
    public void initialize(Unique unique) {
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
        return !service.fieldValueExists(o, fieldName);
    }
}
