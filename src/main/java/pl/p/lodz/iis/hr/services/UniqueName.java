package pl.p.lodz.iis.hr.services;

import pl.p.lodz.iis.hr.repositories.FindByNameProvider;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Unique name validation.<br/>
 * Based on: http://codingexplained.com/coding/java/hibernate/unique-field-validation-using-hibernate-spring
 *
 * @author February 27, 2015 by Bo Andersen
 * @author modifications by 180254
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueNameValidator.class)
@Documented
public @interface UniqueName {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends FindByNameProvider<?>> service();

    String serviceQualifier() default "";
}
