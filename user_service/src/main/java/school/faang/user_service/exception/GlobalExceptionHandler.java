package school.faang.user_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnknownException(Exception exception) {
        log.error("Неизвестная ошибка. Срочно зовите фиксиков.");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Неизвестная ошибка, обратитесь к администратору");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException exception) {
        log.error("Пользователь не найден", exception);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler(MentorshipNotFoundException.class)
    public ResponseEntity<String> handleMentorshipNotFoundException(MentorshipNotFoundException exception) {
        log.error("Менторская связь не найдена", exception);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler(RecommendationRequestNotFoundException.class)
    public ResponseEntity<String> handleRecommendationRequestNotFoundException(RecommendationRequestNotFoundException exception) {
        log.error("Запрос рекоммендации не найден", exception);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleDataValidationException(DataValidationException exception) {
        log.error("Некорректный запрос.", exception);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<String> handleForbiddenException(ForbiddenException exception) {
        log.error("Невозможно выполнить данное действие.", exception);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(exception.getMessage());
    }
}
