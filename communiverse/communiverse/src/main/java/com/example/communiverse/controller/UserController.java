package com.example.communiverse.controller;

import com.example.communiverse.domain.Community;
import com.example.communiverse.domain.User;
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
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Obtains User by ID")
    @GetMapping("/{id}")
    public ResponseEntity<User> getTop5CommunitiesByFollowers(@PathVariable String id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return ResponseEntity.ok(user);
    }
}
