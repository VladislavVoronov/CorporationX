package faang.school.postservice.service.impl;

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
    private final PostRepositoryService postRepositoryService;
    private final CommentRepositoryService commentRepositoryService;
    private final CommentMapper commentMapper;

    @Override
    public long createComment(long postId, String context) {
        UserDto userDto = userContext.getUserById(userContext.getUserId());
        Post post = postRepositoryService.findPostById(postId);

        if (post.isDeleted() && !post.isPublished()) {
            throw new ForbiddenException("Можно добавить комментарий только к опубликованному и не удалённому посту");
        }

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
        UserDto userDto = userContext.getUserById(userContext.getUserId());
        Comment comment = commentRepositoryService.findCommentById(commentId);
        Post post = postRepositoryService.findPostById(comment.getPost().getId());

        if (!Objects.equals(userDto.getUserId(), comment.getAuthorId())) {
            throw new ForbiddenException("Обновлять комментарий может только его автор");
        }

        if (post.isDeleted() && !post.isPublished()) {
            throw new ForbiddenException("Можно обновить только комментарий только к опубликованному и не удалённому посту");
        }

        comment.setContent(context);
        comment = commentRepositoryService.save(comment);

        log.info("Комментарий ID: {} к посту ID: {} успешно обновлён.", comment.getId(), comment.getPost().getId());

        return comment.getId();
    }

    @Override
    public List<CommentDto> getCommentsByPostId(long postId) {
        Post post = postRepositoryService.findPostById(postId);

        if (post.isDeleted() && !post.isPublished()) {
            throw new ForbiddenException("Можно получить комментарии только к опубликованному и не удалённому посту");
        }

        List<Comment> comments = commentRepositoryService.findCommentsByPostId(postId);

        return comments.stream()
                .map(commentMapper::mapToCommentDto)
                .toList();
    }

    @Override
    public void deleteCommentById(long commentId) {
        Comment comment = commentRepositoryService.findCommentById(commentId);
        Post post = postRepositoryService.findPostById(comment.getPost().getId());

        if (!Objects.equals(userContext.getUserId(), comment.getAuthorId())) {
            throw new ForbiddenException("Удалить комментарий может только его автор");
        }

        if (post.isDeleted() && !post.isPublished()) {
            throw new ForbiddenException("Можно удалить комментарий только к опубликованному и не удалённому посту");
        }

        post.getComments().remove(comment);
        postRepositoryService.save(post);

        commentRepositoryService.delete(comment);
    }
}
