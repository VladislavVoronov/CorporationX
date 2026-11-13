package faang.school.postservice.exception;

import jakarta.persistence.EntityNotFoundException;

public class PostNotFoundException extends EntityNotFoundException {
    public PostNotFoundException(String message) {
        super(message);
    }
}
