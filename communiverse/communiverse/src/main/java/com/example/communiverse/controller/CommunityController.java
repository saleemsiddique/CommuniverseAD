package com.example.communiverse.controller;

import com.example.communiverse.domain.Community;
import com.example.communiverse.service.CommunityService;
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

    @Operation(summary = "Obtains top 5 most popular communities")
    @GetMapping("/top5")
    public ResponseEntity<List<Community>> getTop5CommunitiesByFollowers() {
        List<Community> top5Communities = communityService.findTop5ByOrderByFollowersDesc();
        return ResponseEntity.ok(top5Communities);
    }
    @Operation(summary = "Creates community")
    @PostMapping("")
    public ResponseEntity<Community> createCommunity(@RequestBody Community community) {
        Community createdCommunity = communityService.createCommunity(community);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCommunity);
    }
}
