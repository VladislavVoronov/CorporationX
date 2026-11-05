package school.faang.user_service.exception;

public class SkillNotFoundException extends EntityNotFoundException {
    public SkillNotFoundException(String message) {
        super(message);
    }
}
