package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;

import java.util.List;

public interface PostService {

    long createPostDraft(String content);

    long publishPost(long postId);

    long updatePost(long postId, String content);

    long deletePost(long postId);

    PostDto getPostById(long postId);

    List<PostDto> getPostDraftsByAuthorId(long authorId);

    List<PostDto> getPublishedPostsByAuthorId(long authorId);
}
