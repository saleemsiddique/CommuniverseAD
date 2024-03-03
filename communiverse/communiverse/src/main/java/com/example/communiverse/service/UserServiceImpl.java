package com.example.communiverse.service;

import com.example.communiverse.domain.User;
import com.example.communiverse.repository.UserRepository;
import com.example.communiverse.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public User addUser(User user) {
        user.setId(IdGenerator.generateUserId());
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }
}
