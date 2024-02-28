package com.example.communiverse.service;

import com.example.communiverse.domain.Community;

import java.util.List;

public interface CommunityService {
    List<Community> findTop5ByOrderByFollowersDesc();
    Community createCommunity(Community community);
}
