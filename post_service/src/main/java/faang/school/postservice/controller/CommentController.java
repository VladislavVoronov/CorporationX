package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequestMapping("/comments")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create/{postId}")
    public ResponseEntity<Long> createComment(@PathVariable long postId, @RequestBody @NotBlank @Size(max = 4096) String context) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentService.createComment(postId, context));
    }

    @PutMapping("/update/{commentId}")
    public ResponseEntity<Long> updateComment(@PathVariable long commentId, @RequestBody @NotBlank @Size(max = 4096) String context) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(commentService.updateComment(commentId, context));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable long postId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(commentService.getCommentsByPostId(postId));
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable long commentId) {
        commentService.deleteCommentById(commentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
