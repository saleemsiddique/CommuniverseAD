package com.example.communiverse.repository;

import com.example.communiverse.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findById(String id);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
}
