package com.example.communiverse.service;

import com.example.communiverse.domain.Community;
import com.example.communiverse.domain.User;
import com.example.communiverse.repository.CommunityRepository;
import com.example.communiverse.repository.UserRepository;
import com.example.communiverse.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Override
    public User addUser(User user) {
        user.setId(IdGenerator.generateId());
        return userRepository.save(user);
    }

    public List<String> getAllCommunityIds(User user) {
        List<String> allCommunityIds = new ArrayList<>();

        // Add all community IDs from createdCommunities
        allCommunityIds.addAll(user.getCreatedCommunities());

        // Add all community IDs from moderatedCommunities
        allCommunityIds.addAll(user.getModeratedCommunities());

        // Add all community IDs from memberCommunities
        allCommunityIds.addAll(user.getMemberCommunities());

        return allCommunityIds;
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findByUsernameRegex(String usernamePattern) {
        return userRepository.findByUsernameRegex(usernamePattern);
    }

    @Override
    public User follow(User followingUser, User followedUser) {
        if (followingUser.getFollowed_id().contains(followedUser.getId()) && followedUser.getFollowers_id().contains(followingUser.getId())) {
            // Si ya se siguen
            followingUser.getFollowed_id().remove(followedUser.getId());
            followedUser.getFollowers_id().remove(followingUser.getId());
        } else {
            // Si no se siguen
            followingUser.getFollowed_id().add(followedUser.getId());
            followedUser.getFollowers_id().add(followingUser.getId());
        }
        userRepository.save(followingUser);
        return userRepository.save(followedUser);
    }


    @Override
    public User joinCommunity(Community community, User user) {
        if (user.getCreatedCommunities().remove(community.getId()) || user.getModeratedCommunities().remove(community.getId()) || user.getMemberCommunities().remove(community.getId())) {
            community.setFollowers(community.getFollowers() - 1);
        } else {
            user.getMemberCommunities().add(community.getId());
            community.setFollowers(community.getFollowers() + 1);
        }
        communityRepository.save(community);
        return userRepository.save(user);
    }

    @Override
    public List<User> findByMemberCommunitiesContaining(String communityId) {
        return userRepository.findByMemberCommunitiesContainingOrModeratedCommunitiesContainingOrCreatedCommunitiesContaining(communityId, communityId, communityId);
    }
    @Override
    public List<User> removeUserFromCommunity(String userId, String communityId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Community community = communityRepository.findById(communityId).orElseThrow(() -> new IllegalArgumentException("Comunidad no encontrado"));
        if (user.getCreatedCommunities().remove(communityId) || user.getModeratedCommunities().remove(communityId) || user.getMemberCommunities().remove(communityId)) {
            community.setFollowers(community.getFollowers() - 1);
            userRepository.save(user);
            communityRepository.save(community);
            return findByMemberCommunitiesContaining(communityId);
        } else {
            throw new IllegalArgumentException("Usuario no encontrado en la comunidad");
        }
    }

    @Override
    public List<User> promote(String userId, String communityId, String idCreator) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Community community = communityRepository.findById(communityId).orElseThrow(() -> new IllegalArgumentException("Comunidad no encontrado"));

        User creatorUser = userRepository.findById(idCreator)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (user.getMemberCommunities().contains(communityId)) {
            user.getMemberCommunities().remove(communityId);
            user.getModeratedCommunities().add(communityId);
            userRepository.save(user);
            return findByMemberCommunitiesContaining(communityId);
        } else if (user.getModeratedCommunities().contains(communityId)) {
            user.getModeratedCommunities().remove(communityId);
            user.getCreatedCommunities().add(communityId);
            creatorUser.getCreatedCommunities().remove(communityId);
            creatorUser.getModeratedCommunities().add(communityId);
            userRepository.save(creatorUser);
            userRepository.save(user);
            return findByMemberCommunitiesContaining(communityId);
        }
        else {
            throw new IllegalArgumentException("Usuario no es miembro de la comunidad");
        }
    }
    @Override
    public List<User> demoteToMember(String userId, String communityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (user.getModeratedCommunities().contains(communityId)) {
            user.getModeratedCommunities().remove(communityId);
            user.getMemberCommunities().add(communityId);
            userRepository.save(user);
            return findByMemberCommunitiesContaining(communityId);
        } else {
            throw new IllegalArgumentException("Usuario no es moderador de la comunidad");
        }
    }

}
