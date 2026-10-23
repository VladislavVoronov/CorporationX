package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserDtoForUpdate;

public interface UserService {

    UserDto createUser(UserDto userDto);
    UserDto updateUser(UserDtoForUpdate userDtoForUpdate, long userId);
    UserDto deleteUser(long userId);
    UserDto getUser(long userId);
}
