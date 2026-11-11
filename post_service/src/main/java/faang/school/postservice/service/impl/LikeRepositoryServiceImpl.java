package faang.school.postservice.service.impl;

import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.LikeNotFoundException;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.LikeRepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeRepositoryServiceImpl implements LikeRepositoryService {

    private final LikeRepository likeRepository;

    @Override
    public Like findLikeByCommentIdAndUserId(long commentId, long userId) {
        return likeRepository.findByCommentIdAndUserId(commentId, userId).orElseThrow(
                () -> new CommentNotFoundException("Коментарий не найден"));
    }

    @Override
    public Like findById(long likeId) {
        return likeRepository.findById(likeId).orElseThrow(
                () -> new LikeNotFoundException("Лайк не найден"));
    }

    @Override
    public void deleteLike(Like like) {
        likeRepository.delete(like);
        log.info("Лайк успешно удалён. ID лайка: {}.", like.getId());
    }

    @Override
    public boolean existsByPostIdAndUserId(long postId, long userId) {
        return likeRepository.existsByPostIdAndUserId(postId, userId);
    }
}
