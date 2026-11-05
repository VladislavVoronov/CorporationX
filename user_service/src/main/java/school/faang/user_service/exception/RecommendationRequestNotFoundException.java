package school.faang.user_service.exception;

public class RecommendationRequestNotFoundException extends EntityNotFoundException {
    public RecommendationRequestNotFoundException(String message) {
        super(message);
    }
}
