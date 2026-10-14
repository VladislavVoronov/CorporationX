package school.faang.user_service.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.user.User;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "countryId", target = "country.id")
    User mapToUser(UserDto userDto);

    @Mapping(source = "country.id", target = "countryId")
    UserDto mapToUserDto(User user);
}


