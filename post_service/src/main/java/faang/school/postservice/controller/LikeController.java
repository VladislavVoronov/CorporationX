package faang.school.postservice.controller;

import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/likes")
@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/likepost/{postId}")
    public ResponseEntity<Long> likePost(@PathVariable long postId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(likeService.likePost(postId));
    }

    @PostMapping("/likecomment/{commentId}")
    public ResponseEntity<Long> likeComment(@PathVariable long commentId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(likeService.likeComment(commentId));
    }

    @DeleteMapping("/postdelete/{likeId}")
    public ResponseEntity<Long> deleteLikePost(@PathVariable long likeId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(likeService.deletePostLike(likeId));
    }

    @DeleteMapping("/commentdelete/{likeId}")
    public ResponseEntity<Long> deleteLikeComment(@PathVariable long likeId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(likeService.deleteCommentLike(likeId));
    }
}
