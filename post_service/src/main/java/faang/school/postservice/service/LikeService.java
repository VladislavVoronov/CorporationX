package faang.school.postservice.service;

import faang.school.postservice.model.Like;

public interface LikeService {

    long likePost(long PostId);

    long likeComment(long commentId);

    long deletePostLike(long likeId);

    long deleteCommentLike(long likeId);

    void validateLikeAuthor(Like like);
}
