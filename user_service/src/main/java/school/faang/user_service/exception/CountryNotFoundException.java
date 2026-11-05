package school.faang.user_service.exception;

public class CountryNotFoundException extends EntityNotFoundException {
    public CountryNotFoundException(String message) {
        super(message);
    }
}
