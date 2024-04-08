package com.example.communiverse.service;

import com.example.communiverse.domain.User;
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
}
