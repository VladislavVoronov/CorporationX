package school.faang.user_service.dto;

import lombok.Data;

@Data
public class RecommendationDto {

    private String content;
    private long authorId;
    private long receiverId;

}
