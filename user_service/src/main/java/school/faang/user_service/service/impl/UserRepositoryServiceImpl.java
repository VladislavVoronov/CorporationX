package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.UserRepositoryService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRepositoryServiceImpl implements UserRepositoryService {

    private static final String MESSAGE_USER_NOT_FOUND = "User ID: %s not found";
    private final UserRepository userRepository;

    @Override
    public User findById(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(MESSAGE_USER_NOT_FOUND.formatted(userId)));
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("Username %s not found".formatted(username)));
    }

    @Override
    public User save(User user) {
        log.info("Пользователь успешно сохранён. ID пользователя: {}.", user.getId());
        return userRepository.save(user);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
        log.info("Пользователь успешно удалён. ID пользователя: {}.", user.getId());
    }
}
