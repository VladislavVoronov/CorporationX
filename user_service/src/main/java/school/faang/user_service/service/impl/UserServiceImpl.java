package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.UserService;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {

        Country country = countryRepository.findById(userDto.getCountryId()).orElseThrow(
                () -> new EntityNotFoundException("Country not found")
        );

        User user = userMapper.mapToUser(userDto);
        user.setActive(true);
        user = userRepository.save(user);
        country.getResidents().add(user);
        countryRepository.save(country);

        userDto = userMapper.mapToUserDto(user);

        return userDto;
    }
}
