package faang.school.postservice.config.context;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.UserNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Component
public class UserContext {

    private final UserServiceClient userServiceClient;
    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        return userIdHolder.get();
    }

    public void clear() {
        userIdHolder.remove();
    }

    public UserDto getUserById(long userId) {
        UserDto userDto;

        try {
            userDto = userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException("Пользователь ID:%s не найден".formatted(userId));

        } catch (FeignException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Не удалось получить данные пользователя. Попробуйте позже.", e);
        }

        return userDto;
    }
}
