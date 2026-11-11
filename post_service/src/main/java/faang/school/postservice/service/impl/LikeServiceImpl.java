package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final UserClient userClient;
    private final UserContext userContext;
    private final LikeRepository likeRepository;
    private final PostRepositoryService postRepositoryService;
    private final LikeRepositoryService likeRepositoryService;
    private final CommentRepositoryService commentRepositoryService;

    @Override
    public long likePost(long postId) {
        UserDto userDto = userClient.getUserById(userContext.getUserId());
        Post post = postRepositoryService.findPostById(postId);

        if (likeRepositoryService.existsByPostIdAndUserId(postId, userDto.getUserId())) {
            throw new ForbiddenException("Под данным постом уже стоит лайк");
        }

        Like like = Like.builder()
                .userId(userDto.getUserId())
                .post(post)
                .comment(null)
                .build();
        like = likeRepository.save(like);

        post.getLikes().add(like);
        post = postRepositoryService.save(post);

        log.info("Поставлен лайк ID: {} к посту ID: {}.", like.getId(), post.getId());

        return like.getId();
    }

    @Override
    public long likeComment(long commentId){
        UserDto userDto = userClient.getUserById(userContext.getUserId());
        Comment comment = commentRepositoryService.findCommentById(commentId);

        if (likeRepository.existsByCommentIdAndUserId(commentId, userDto.getUserId())) {
            throw new ForbiddenException("Под данным комментарием уже стоит лайк");
        }

        Like like = Like.builder()
                .userId(userDto.getUserId())
                .post(null)
                .comment(comment)
                .build();
        like = likeRepository.save(like);

        comment.getLikes().add(like);
        comment = commentRepositoryService.save(comment);

        log.info("Поставлен лайк ID: {} к комментарию ID: {}.", like.getId(), comment.getId());

        return like.getId();
    }

    @Override
    public long deletePostLike(long likeId) {
        userClient.getUserById(userContext.getUserId());
        Like like = likeRepositoryService.findById(likeId);
        Post post = postRepositoryService.findPostById(like.getPost().getId());
        validateLikeAuthor(like);

        if (!post.getLikes().contains(like)) {
            throw new ForbiddenException("Под данным постом не стоит лайк");
        }

        post.getLikes().remove(like);
        postRepositoryService.save(post);
        likeRepositoryService.deleteLike(like);

        log.info("Удалён лайк ID: {} к посту ID: {}.", like.getId(), post.getId());

        return like.getId();
    }

    @Override
    public long deleteCommentLike(long likeId) {
        userClient.getUserById(userContext.getUserId());
        Like like = likeRepositoryService.findById(likeId);
        Comment comment = commentRepositoryService.findCommentById(like.getComment().getId());
        validateLikeAuthor(like);

        if (!comment.getLikes().contains(like)) {
            throw new ForbiddenException("Под данным комментарием не стоит лайк");
        }

        comment.getLikes().remove(like);
        commentRepositoryService.save(comment);
        likeRepositoryService.deleteLike(like);

        log.info("Удалён лайк ID: {} к комментарию ID: {}.", like.getId(), comment.getId());

        return like.getId();
    }

    @Override
    public void validateLikeAuthor(Like like) {
        if (like.getUserId() != userContext.getUserId()) {
            throw new ForbiddenException("Изменять лайк может только его автор!");
        }
    }
}
