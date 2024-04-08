package com.example.communiverse.service;

import com.example.communiverse.domain.User;
import com.example.communiverse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User addUser(User user);

    public List<String> getAllCommunityIds(User user);

    Optional<User> findById(String id);

    Optional<User> findByUsername(String username);

}
