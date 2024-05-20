package com.example.communiverse.service;

import com.example.communiverse.domain.Community;
import com.example.communiverse.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User addUser(User user);

    public List<String> getAllCommunityIds(User user);

    Optional<User> findById(String id);

    Optional<User> findByUsername(String username);

    List<User> findByUsernameRegex(String usernamePattern);

    User follow(User followingUser, User followedUser);

    User joinCommunity(Community community, User user);

    List<User> findByMemberCommunitiesContaining(String communityId);

    List<User> removeUserFromCommunity(String userId, String communityId, int daysUntilBan);
    List<User> promote(String userId, String communityId, String idCreator);
    List<User> demoteToMember(String userId, String communityId);

    String recuperatePassword(String email);
}
