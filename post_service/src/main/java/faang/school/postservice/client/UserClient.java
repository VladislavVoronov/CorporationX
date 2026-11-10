package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.UserNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Component
public class UserClient {

    private final UserServiceClient userServiceClient;

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
