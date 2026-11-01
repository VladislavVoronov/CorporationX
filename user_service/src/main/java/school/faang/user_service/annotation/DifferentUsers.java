package school.faang.user_service.annotation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import school.faang.user_service.validation.DifferentUsersValidator;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DifferentUsersValidator.class)
public @interface DifferentUsers {

    String message() default "Нельзя запросить рекомендацию у самого себя";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}