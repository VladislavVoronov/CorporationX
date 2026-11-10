package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequestMapping("/posts")
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<Long> createPostDraft(@RequestBody @NotBlank @Size(max = 4096) String content) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postService.createPostDraft(content));
    }

    @PutMapping("/publish/{postId}")
    public ResponseEntity<Long> publishPost(@PathVariable long postId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postService.publishPost(postId));
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<Long> updatePost(@PathVariable long postId, @RequestBody @NotBlank @Size(max = 4096) String content) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postService.updatePost(postId, content));
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Long> deletePost(@PathVariable long postId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postService.deletePost(postId));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable long postId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postService.getPostById(postId));
    }

    @GetMapping("/drafts/{authorId}")
    public ResponseEntity<List<PostDto>> getPostDraftsByAuthorId(@PathVariable long authorId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postService.getPostDraftsByAuthorId(authorId));
    }

    @GetMapping("/published/{authorId}")
    public ResponseEntity<List<PostDto>> getPostsByAuthorId(@PathVariable long authorId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postService.getPostsByAuthorId(authorId));
    }
}
