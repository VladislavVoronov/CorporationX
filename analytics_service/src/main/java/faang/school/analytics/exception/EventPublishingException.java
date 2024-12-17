package faang.school.analytics.exception;

public class EventPublishingException extends RuntimeException {

    public EventPublishingException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventPublishingException(String message) {
        super(message);
    }
}
