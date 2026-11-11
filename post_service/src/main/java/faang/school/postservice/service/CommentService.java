package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;

import java.util.List;

public interface CommentService {

    long createComment(long postId, String context);

    long updateComment(long commentId, String context);

    List<CommentDto> getCommentsByPostId(long postId);

    void deleteCommentById(long commentId);

    void validateAuthorByCommentId(long commentId);
}
