package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.UserRepositoryService;

@Service
@RequiredArgsConstructor
public class UserRepositoryServiceImpl implements UserRepositoryService {

    private static final String MESSAGE_USER_NOT_FOUND = "User not found";
    private final UserRepository userRepository;

    @Override
    public User findById(long userId) {
        return userRepository.findById(userId).orElseThrow(
                ()-> new EntityNotFoundException(MESSAGE_USER_NOT_FOUND)
        );
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(MESSAGE_USER_NOT_FOUND)
        );
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }
}
