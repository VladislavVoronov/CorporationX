package faang.school.postservice.dto.comment;

import faang.school.postservice.dto.like.LikeDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDto {

    private Long id;

    private Long authorId;

    private String content;

    private List<LikeDto> likes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
