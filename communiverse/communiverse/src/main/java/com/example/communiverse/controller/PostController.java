package com.example.communiverse.controller;

import com.example.communiverse.domain.Post;
import com.example.communiverse.exception.PostNotFoundException;
import com.example.communiverse.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;


    @Operation(summary = "Obtains Post by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable String id) {
        Post post = postService.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        return ResponseEntity.ok(post);
    }

    @Operation(summary = "Obtains all posts from one user")
    @GetMapping("/posts/{id}/{page}/{size}")
    public ResponseEntity<List<Post>> getMyPosts(@PathVariable String id,
                                                 @PathVariable int page,
                                                 @PathVariable int size) {
        List<Post> myPosts = postService.findByAuthor_IdAndIsCommentFalseOrderByDateTimeDesc(id, page, size);
        return ResponseEntity.ok(myPosts);
    }

        @Operation(summary = "Obtains all reposts from one user")
    @GetMapping("/reposts/{id}/{page}/{size}")
    public ResponseEntity<List<Post>> getMyRePostsPaged(@PathVariable String id,
                                                   @PathVariable int page,
                                                   @PathVariable int size) {
        List<Post> myRePosts = postService.findAllByRepostUserIdPaged(id, page, size);
        return ResponseEntity.ok(myRePosts);
    }

    @Operation(summary = "Obtains all comments from one post")
    @GetMapping("/comments/{postId}/{page}/{size}")
    public ResponseEntity<List<Post>> getCommentsByPostId(
            @PathVariable String postId,
            @PathVariable int page,
            @PathVariable int size) {
        List<Post> comments = postService.getCommentsByPostId(postId, page, size);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "Creates a post")
    @PostMapping("")
    public ResponseEntity<Post> addPost(@RequestBody Post post){
        Post createdPost = postService.addPost(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }
}
