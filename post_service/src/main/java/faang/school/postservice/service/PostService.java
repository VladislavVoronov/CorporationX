package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;

import java.util.List;

public interface PostService {

    long createPostDraft(String content);

    long publishPost(long postId);

    long updatePost(long postId, String content);

    long deletePost(long postId);

    PostDto getPostById(long postId);

    List<PostDto> getPostDraftsByAuthorId(long authorId);

    List<PostDto> getPostsByAuthorId(long authorId);

    void validatePublishPost(Post post);

    void validatePostAuthor(Post post);

    void validateDeletedPostById(long postId);
}
