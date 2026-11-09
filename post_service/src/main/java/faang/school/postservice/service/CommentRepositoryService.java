package faang.school.postservice.service;

import faang.school.postservice.model.Comment;

import java.util.List;

public interface CommentRepositoryService {

    Comment findCommentById(long commentId);

    Comment save(Comment comment);

    void delete(Comment comment);

    List<Comment> findCommentsByPostId(long postId);
}
