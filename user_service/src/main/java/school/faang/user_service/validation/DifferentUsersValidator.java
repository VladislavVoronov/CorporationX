package school.faang.user_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import school.faang.user_service.annotation.DifferentUsers;
import school.faang.user_service.dto.RecommendationRequestDto;

public class DifferentUsersValidator
        implements ConstraintValidator<DifferentUsers, RecommendationRequestDto> {

    @Override
    public boolean isValid(
            RecommendationRequestDto dto,
            ConstraintValidatorContext context
    ) {
        if (dto == null) {
            return true;
        }

        return dto.getRequesterId() != dto.getReceiverId();
    }
}