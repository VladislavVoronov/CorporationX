package school.faang.user_service.mapper;


import org.mapstruct.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserDtoForUpdate;
import school.faang.user_service.entity.user.User;


@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(source = "countryId", target = "country.id")
    User mapToUser(UserDto userDto);

    @Mapping(source = "country.id", target = "countryId")
    UserDto mapToUserDto(User user);

    @Mapping(source = "countryId", target = "country.id", ignore = true)
    void updatedUser(UserDtoForUpdate userDtoForUpdate, @MappingTarget User user);

}


