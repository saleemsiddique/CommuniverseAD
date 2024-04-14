package com.example.communiverse.controller;

import com.example.communiverse.domain.Post;
import com.example.communiverse.domain.User;
import com.example.communiverse.exception.PostNotFoundException;
import com.example.communiverse.exception.UserNotFoundException;
import com.example.communiverse.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Obtains User by ID")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Obtains User by Username")
    @GetMapping("username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Obtains User by Username")
    @GetMapping("usernameRegex/{username}")
    public ResponseEntity<List<User>> getCommentsByPostId(
            @PathVariable String username) {
        List<User> searchedUsers = userService.findByUsernameRegex(username);
        return ResponseEntity.ok(searchedUsers);
    }

    @Operation(summary = "following system")
    @GetMapping("/follow/{idFollowing}/{idFollowed}")
    public ResponseEntity<User> follow(@PathVariable String idFollowing, @PathVariable String idFollowed) {
        User followingUser = userService.findById(idFollowing)
                .orElseThrow(() -> new PostNotFoundException(idFollowing));

        User followedUser = userService.findById(idFollowed)
                .orElseThrow(() -> new PostNotFoundException(idFollowed));

        userService.follow(followingUser, followedUser);

        return ResponseEntity.ok(followedUser);
    }
}
