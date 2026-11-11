package faang.school.postservice.service.impl;

import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.CommentRepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentRepositoryServiceImpl implements CommentRepositoryService {

    private final CommentRepository commentRepository;

    @Override
    public Comment findCommentById(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new CommentNotFoundException("Комментарий ID: %s не найден!".formatted(commentId)));
    }

    @Override
    public Comment save(Comment comment) {
        comment = commentRepository.save(comment);
        log.info("Комментарий успешно сохранён. ID комментария: {}.", comment.getId());

        return comment;
    }

    @Override
    public void delete(Comment comment) {
        commentRepository.delete(comment);
        log.info("Комментарий успешно удалён. ID комментария: {}.", comment.getId());
    }

    @Override
    public List<Comment> findCommentsByPostId(long postId){
        return commentRepository.findCommentsByPostId(postId);
    }

    public boolean existsByCommentIdAndUserId(long commentId, long userId) {
        return commentRepository.existsByIdAndAuthorId(commentId, userId);
    }
}
