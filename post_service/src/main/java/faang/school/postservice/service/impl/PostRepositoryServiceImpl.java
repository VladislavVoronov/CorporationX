package faang.school.postservice.service.impl;

import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostRepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostRepositoryServiceImpl implements PostRepositoryService {

    private final PostRepository postRepository;

    @Override
    public Post findPostById(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("Пост ID: %s не найден!".formatted(postId)));

        if (post.isDeleted()) {
            throw new PostNotFoundException("Пост ID: %s находится в статусе удалён!".formatted(postId));
        }

        return post;
    }

    @Override
    public Post save(Post post) {
        post = postRepository.save(post);
        log.info("Пост успешно сохранён. ID поста: {}.", post.getId());

        return post;
    }

    @Override
    public void delete(Post post) {
       postRepository.delete(post);
        log.info("Пост успешно удалён. ID поста: {}.", post.getId());
    }
}
