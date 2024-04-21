package com.example.communiverse.controller;

import com.example.communiverse.domain.Community;
import com.example.communiverse.domain.User;
import com.example.communiverse.exception.CommunityNotFoundException;
import com.example.communiverse.service.CommunityService;
import com.example.communiverse.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community")
public class CommunityController {
    @Autowired
    private CommunityService communityService;
    @Autowired
    private UserService userService;

    @Operation(summary = "Obtains Community by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Community> getCommunityById(@PathVariable String id) {
        Community community = communityService.findById(id)
                .orElseThrow(() -> new CommunityNotFoundException(id));
        return ResponseEntity.ok(community);
    }


    @Operation(summary = "Obtains top 5 most popular communities")
    @GetMapping("/top5")
    public ResponseEntity<List<Community>> getTop5CommunitiesByFollowers() {
        List<Community> top5Communities = communityService.findTop5ByOrderByFollowersDesc();
        return ResponseEntity.ok(top5Communities);
    }

    @GetMapping("/mycommunities/{id}")
    public List<Community> getMyCommunities(@PathVariable String id) {
        // Get user by ID
        User user = userService.findById(id).orElse(null);
        if (user == null) {
            // Handle user not found
            return null;
        }

        // Get all community IDs associated with the user
        List<String> communityIds = userService.getAllCommunityIds(user);

        return communityService.getMyCommunities(communityIds);
    }

    @Operation(summary = "Creates community")
    @PostMapping("")
    public ResponseEntity<Community> createCommunity(@RequestBody Community community) {
        Community createdCommunity = communityService.createCommunity(community);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCommunity);
    }

    @DeleteMapping("/{communityId}")
    public ResponseEntity<User> deleteCommunity(@PathVariable String communityId) {
        Community community = communityService.findById(communityId)
                .orElseThrow(() -> new CommunityNotFoundException(communityId));
        User user = communityService.deleteCommunity(community);
        return ResponseEntity.ok(user);
    }
}
