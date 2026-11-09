package faang.school.postservice.service;

import faang.school.postservice.model.Post;

public interface PostRepositoryService {

    Post findPostById(long postId);

    Post save(Post post);
}
