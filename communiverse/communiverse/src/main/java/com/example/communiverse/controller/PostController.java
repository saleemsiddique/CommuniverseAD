package com.example.communiverse.controller;

import com.example.communiverse.domain.Community;
import com.example.communiverse.domain.Post;
import com.example.communiverse.domain.User;
import com.example.communiverse.exception.PostNotFoundException;
import com.example.communiverse.exception.UserNotFoundException;
import com.example.communiverse.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Post> getTop5CommunitiesByFollowers(@PathVariable String id) {
        Post post = postService.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        return ResponseEntity.ok(post);
    }

    @Operation(summary = "Obtains all posts from one user")
    @GetMapping("/posts/{id}/{page}/{size}")
    public ResponseEntity<Page<Post>> getMyPosts(@PathVariable String id,
                                                 @PathVariable int page,
                                                 @PathVariable int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> myPosts = postService.findByAuthor_IdOrderByDateTimeDesc(id, pageable);
        return ResponseEntity.ok(myPosts);
    }

        @Operation(summary = "Obtains all reposts from one user")
    @GetMapping("/reposts/{id}/{page}/{size}")
    public ResponseEntity<Page<Post>> getMyRePostsPaged(@PathVariable String id,
                                                   @PathVariable int page,
                                                   @PathVariable int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> myRePosts = postService.findAllByRepostUserIdPaged(id, pageable);
        return ResponseEntity.ok(myRePosts);
    }


    @Operation(summary = "Creates a post")
    @PostMapping("")
    public ResponseEntity<Post> addPost(@RequestBody Post post){
        Post createdPost = postService.addPost(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }
}
