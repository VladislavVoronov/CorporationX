package faang.school.postservice.dto.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private Long id;

    private Long authorId;

    private String content;

    private int countLikes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
