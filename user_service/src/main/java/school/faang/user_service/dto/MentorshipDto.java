package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MentorshipDto {

    private long mentorId;
    private long menteeId;
}
