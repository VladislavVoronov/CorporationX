package school.faang.user_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.annotation.DifferentUsers;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.RecommendationRequestDto;

@RequiredArgsConstructor
public class DifferentUsersValidator
        implements ConstraintValidator<DifferentUsers, RecommendationRequestDto> {

    private final UserContext userContext;

    @Override
    public boolean isValid(
            RecommendationRequestDto dto,
            ConstraintValidatorContext context
    ) {
        if (dto == null) {
            return true;
        }

        return userContext.getUserId() != dto.getReceiverId();
    }
}