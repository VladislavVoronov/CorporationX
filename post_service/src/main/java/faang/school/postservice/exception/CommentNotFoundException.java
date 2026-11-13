package faang.school.postservice.exception;

import jakarta.persistence.EntityNotFoundException;

public class CommentNotFoundException extends EntityNotFoundException {
    public CommentNotFoundException(String message) {
        super(message);
    }
}
