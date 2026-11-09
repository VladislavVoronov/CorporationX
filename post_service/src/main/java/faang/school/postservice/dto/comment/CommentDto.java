package faang.school.postservice.dto.comment;

import faang.school.postservice.model.Like;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDto {

    private Long authorId;

    private String content;

    private List<Like> likes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
