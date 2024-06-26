package com.example.communiverse.service;

import com.example.communiverse.domain.Community;
import com.example.communiverse.domain.User;

import java.util.List;
import java.util.Optional;

public interface CommunityService {

    Optional<Community> findById(String id);
    List<Community> findTop5ByOrderByFollowersDesc();

    List<Community> findByNameRegex(String namePattern);

    public List<Community> getMyCommunities(List<String> communityIds);

    Community createCommunity(Community community);

    Community updateCommunity(String id, Community community);

    User deleteCommunity(Community community);

    List<User> getBannedUsers(Community community);

    Community unbanUser(String communityId, String userId);
}
