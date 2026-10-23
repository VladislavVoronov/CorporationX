package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SkillDto {

    @NotBlank
    @Size(max = 64)
    private String title;

    @NotBlank
    private String username;

}


