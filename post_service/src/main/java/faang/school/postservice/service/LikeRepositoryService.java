package faang.school.postservice.service;

import faang.school.postservice.model.Like;

public interface LikeRepositoryService {

    Like findById(long likeId);

    void deleteLike(Like like);

    boolean existsByPostIdAndUserId(long postId, long userId);

    boolean existsByCommentIdAndUserId(long commentId, long userId);
}
