package com.example.communiverse.service;

import com.example.communiverse.domain.Community;

import java.util.List;
import java.util.Optional;

public interface CommunityService {

    Optional<Community> findById(String id);
    List<Community> findTop5ByOrderByFollowersDesc();
    Community createCommunity(Community community);
}
