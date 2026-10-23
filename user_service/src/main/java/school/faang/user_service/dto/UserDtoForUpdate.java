package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDtoForUpdate {

    @Size(max = 64)
    private String username;

    @Size(max = 64)
    @Email
    private String email;

    @Size(max = 32)
    private String phone;

    @Size(max = 128)
    private String password;

    private String aboutMe;
    private Long countryId;
    private String city;

    @Positive
    private Integer experience;

}
