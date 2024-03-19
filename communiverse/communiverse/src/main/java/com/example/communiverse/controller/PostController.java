package com.example.communiverse.controller;

import com.example.communiverse.domain.Post;
import com.example.communiverse.exception.PostNotFoundException;
import com.example.communiverse.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
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

    @Operation(summary = "Obtiene todos los posts de una comunidad paginados y ordenados por interacciones")
    @GetMapping("/community/{communityId}/{page}/{size}")
    public ResponseEntity<List<Post>> getAllPostsFromCommunity(
            @PathVariable String communityId,
            @PathVariable int page,
            @PathVariable int size) {
        List<Post> postsFromCommunity = postService.findAllByCommunityIdOrderByInteractionsDesc(communityId, page, size);
        return ResponseEntity.ok(postsFromCommunity);
    }

    @Operation(summary = "Obtiene todos los quizz de una comunidad que tengan quizz paginados y ordenados por interacciones")
    @GetMapping("/community/{communityId}/quizz/{page}/{size}")
    public ResponseEntity<List<Post>> getAllQuizzFromCommunity(
            @PathVariable String communityId,
            @PathVariable int page,
            @PathVariable int size) {
        List<Post> quizzFromCommunity = postService.findAllWithQuizzOrderByInteractionsDesc(communityId, page, size);
        return ResponseEntity.ok(quizzFromCommunity);
    }

    @GetMapping("/community/{communityId}/myspace/{followed}/{page}/{size}")
    public ResponseEntity<List<Post>> getAllPostsFromCommunityMySpace(
            @PathVariable String communityId,
            @PathVariable String followed, // La lista de seguidos como una sola cadena
            @PathVariable int page,
            @PathVariable int size) throws UnsupportedEncodingException {

        // Decodificar la lista de seguidos y dividirla en una lista de Strings
        List<String> followedList = Arrays.asList(URLDecoder.decode(followed, "UTF-8").split(","));

        List<Post> postsFromCommunity = postService.findPostsByCommunityAndFollowedUsers(communityId, followedList, page, size);
        return ResponseEntity.ok(postsFromCommunity);
    }


    @Operation(summary = "Creates a post")
    @PostMapping("")
    public ResponseEntity<Post> addPost(@RequestBody Post post){
        Post createdPost = postService.addPost(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }
}