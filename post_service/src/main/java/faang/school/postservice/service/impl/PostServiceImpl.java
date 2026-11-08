package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;

    @Override
    public long createPostDraft(String content) {
        UserDto userDto;

        try {
            userDto = getUserById(userContext.getUserId());
        } catch (FeignException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Не удалось получить данные пользователя. Попробуйте позже.", e);
        }

        Post post = Post.builder()
                .content(content)
                .authorId(userDto.getUserId())
                .published(false)
                .deleted(false)
                .build();

        post = postRepository.save(post);
        log.info("Черновик поста ID: {} успешно сохранён. ID автора: {}.", post.getId(), post.getAuthorId());

        return post.getId();
    }

    @Override
    public long publishPost(long postId) {
        Post post = findPostById(postId);

        validatePublishPost(post);
        validatePostAuthor(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post = postRepository.save(post);

        log.info("Пост ID: {} успешно опубликован. ID автора: {}.", post.getId(), post.getAuthorId());

        return post.getId();
    }

    @Override
    public long updatePost(long postId, String content) {
        Post post = findPostById(postId);

        validatePostAuthor(post);

        post.setContent(content);
        post = postRepository.save(post);

        log.info("Пост ID: {} успешно обновлен. ID автора: {}.", post.getId(), post.getAuthorId());

        return post.getId();
    }

    @Override
    public long deletePost(long postId) {
        Post post = findPostById(postId);

        validatePostAuthor(post);

        if (post.isDeleted()) {
            throw new ForbiddenException("Пост уже удалён!");
        }

        post.setDeleted(true);
        post.setPublished(false);
        post = postRepository.save(post);

        log.info("Пост ID: {} успешно удалён.", post.getId());

        return post.getId();
    }

    @Override
    public PostDto getPostById(long postId) {
        return postMapper.mapToPostDto(findPostById(postId));
    }

    @Override
    public List<PostDto> getPostDraftsByAuthorId(long authorId) {
        getUserById(authorId);

        return postRepository.findPostDraftsByAuthorId(authorId).stream()
                .map(postMapper::mapToPostDto)
                .toList();
    }

    @Override
    public List<PostDto> getPublishedPostsByAuthorId(long authorId) {
        getUserById(authorId);

        return postRepository.findPublishedPostsByAuthorId(authorId).stream()
                .map(postMapper::mapToPostDto)
                .toList();
    }

    private Post findPostById(long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("Пост ID: %s не найден!".formatted(postId)));
    }

    private UserDto getUserById(long userId) {
        UserDto userDto;

        try {
            userDto = userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException("Пользователь ID:%s не найден".formatted(userContext.getUserId()));
        }

        return userDto;
    }

    private void validatePublishPost(Post post) {
        if (post.isPublished()) {
            throw new ForbiddenException("Пост уже опубликован!");

        } else if (post.isDeleted()) {
            throw new ForbiddenException("Невозможно опубликовать удалённый пост!");
        }
    }

    private void validatePostAuthor(Post post) {
        if (post.getAuthorId() != userContext.getUserId()) {
            throw new ForbiddenException("Изменять пост может только его автор!");
        }
    }
}