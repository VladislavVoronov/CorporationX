package school.faang.user_service.service;

import school.faang.user_service.entity.user.User;

public interface UserRepositoryService {

    User findById(long userId);
    User save(User user);
    void delete(User user);
    User findUserByUsername(String username);
}
