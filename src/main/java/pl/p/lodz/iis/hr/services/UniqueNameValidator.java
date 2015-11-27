package pl.p.lodz.iis.hr.services;

import pl.p.lodz.iis.hr.repositories.FindByNameProvider;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * UniqueName internal helper.
 *
 * @see UniqueName
 */
class UniqueNameValidator implements ConstraintValidator<UniqueName, String> {

    private FindByNameProvider<?> service;

    @Override
    public void initialize(UniqueName constraintAnnotation) {
        Class<? extends FindByNameProvider<?>> clazz = constraintAnnotation.service();
        String serviceQualifier = constraintAnnotation.serviceQualifier();

        service = serviceQualifier.isEmpty()
                ? ApplicationContextProvider.getBean(clazz)
                : ApplicationContextProvider.getBean(serviceQualifier, clazz);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return service.findByName(value) == null;
    }
}
