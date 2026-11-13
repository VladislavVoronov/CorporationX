package faang.school.postservice.dto.post;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDto {

    private long id;

    private Long authorId;

    private String content;

    private boolean published;

    private LocalDateTime publishedAt;

    private int countLikes;
}
