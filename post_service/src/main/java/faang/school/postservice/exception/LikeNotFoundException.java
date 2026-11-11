package faang.school.postservice.exception;

import jakarta.persistence.EntityNotFoundException;

public class LikeNotFoundException extends EntityNotFoundException {
    public LikeNotFoundException(String message) {
        super(message);
    }
}
