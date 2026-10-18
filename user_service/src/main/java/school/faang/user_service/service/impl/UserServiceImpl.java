package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserDtoForUpdate;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.service.UserRepositoryService;
import school.faang.user_service.service.UserService;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepositoryService userRepositoryService;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        Country country = countryRepository.findById(userDto.getCountryId()).orElseThrow(
                () -> new EntityNotFoundException("Country not found")
        );

        User user = userMapper.mapToUser(userDto);
        user.setActive(true);
        user = userRepositoryService.save(user);
        country.getResidents().add(user);
        countryRepository.save(country);

        userDto = userMapper.mapToUserDto(user);

        return userDto;
    }

    @Override
    public UserDto updateUser(UserDtoForUpdate userDtoForUpdate, long userId) {
        User user = userRepositoryService.findById(userId);

        Long oldCountryId = null;
        if (userDtoForUpdate.getCountryId() != null) {
            oldCountryId = user.getCountry().getId();
            Country oldCountry = countryRepository.findById(oldCountryId).orElseThrow(
                    () -> new EntityNotFoundException("Country not found")
            );
            List<User> residents = oldCountry.getResidents();
            residents.remove(user);
            oldCountry.setResidents(residents);
            countryRepository.save(oldCountry);
            user.setCountry(countryRepository.findById(userDtoForUpdate.getCountryId()).orElseThrow(
                    () -> new EntityNotFoundException("Country not found")
            ));
        }

        userMapper.updatedUser(userDtoForUpdate, user);
        user = userRepositoryService.save(user);

        if (oldCountryId != null) {
            Country newCountry = countryRepository.findById(userDtoForUpdate.getCountryId()).orElseThrow(
                    () -> new EntityNotFoundException("Country not found")
            );
            List<User> newResidents = newCountry.getResidents();
            newResidents.add(user);
            newCountry.setResidents(newResidents);
            countryRepository.save(newCountry);
        }

        return userMapper.mapToUserDto(user);
    }

    @Override
    public UserDto deleteUser(long userId) {
        User user = userRepositoryService.findById(userId);
        userRepositoryService.delete(user);

        Country country = countryRepository.findById(user.getCountry().getId()).orElseThrow(
                () -> new EntityNotFoundException("Country not found")
        );
        country.getResidents().remove(user);
        countryRepository.save(country);

        return userMapper.mapToUserDto(user);
    }

    @Override
    public UserDto getUser(long userId) {
        User user = userRepositoryService.findById(userId);
        return userMapper.mapToUserDto(user);
    }
}
