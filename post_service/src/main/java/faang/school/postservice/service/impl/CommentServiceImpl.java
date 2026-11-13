package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.CommentRepositoryService;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.PostRepositoryService;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final UserContext userContext;
    private final UserClient userClient;
    private final PostRepositoryService postRepositoryService;
    private final CommentRepositoryService commentRepositoryService;
    private final CommentMapper commentMapper;
    private final PostService postService;

    @Override
    public long createComment(long postId, String context) {
        UserDto userDto = userClient.getUserById(userContext.getUserId());
        Post post = postRepositoryService.findPostById(postId);
        postService.validateDeletedPostById(postId);

        Comment comment = Comment.builder()
                .content(context)
                .authorId(userDto.getUserId())
                .post(post)
                .build();
        comment = commentRepositoryService.save(comment);

        post.getComments().add(comment);
        post = postRepositoryService.save(post);

        log.info("Комментарий ID: {} к посту ID: {} успешно создан.", comment.getId(), post.getId());

        return comment.getId();
    }

    @Override
    public long updateComment(long commentId, String context) {
        Comment comment = commentRepositoryService.findCommentById(commentId);

        validateAuthorByCommentId(commentId);
        postService.validateDeletedPostById(comment.getPost().getId());

        comment.setContent(context);
        comment = commentRepositoryService.save(comment);

        log.info("Комментарий ID: {} к посту ID: {} успешно обновлён.", comment.getId(), comment.getPost().getId());

        return comment.getId();
    }

    @Override
    public List<CommentDto> getCommentsByPostId(long postId) {
        postService.validateDeletedPostById(postId);

        List<Comment> comments = commentRepositoryService.findCommentsByPostId(postId);

        return comments.stream()
                .map(this::prepareCommentDtoWithSizeLikes)
                .toList();
    }

    @Override
    public void deleteCommentById(long commentId) {
        Comment comment = commentRepositoryService.findCommentById(commentId);
        Post post = postRepositoryService.findPostById(comment.getPost().getId());

        validateAuthorByCommentId(commentId);
        postService.validateDeletedPostById(post.getId());

        post.getComments().remove(comment);
        postRepositoryService.save(post);

        commentRepositoryService.delete(comment);

        log.info("Комментарий ID: {} к посту ID: {} успешно удалён.", comment.getId(), post.getId());
    }

    @Override
    public void validateAuthorByCommentId(long commentId) {
        UserDto userDto = userClient.getUserById(userContext.getUserId());
        Comment comment = commentRepositoryService.findCommentById(commentId);

        if (!Objects.equals(userDto.getUserId(), comment.getAuthorId())) {
            throw new ForbiddenException("Вносить изменения в комментарий может только его автор");
        }
    }

    private CommentDto prepareCommentDtoWithSizeLikes(Comment comment) {
        CommentDto commentDto = commentMapper.mapToCommentDto(comment);
        commentDto.setCountLikes(comment.getLikes().size());
        return commentDto;
    }
}
