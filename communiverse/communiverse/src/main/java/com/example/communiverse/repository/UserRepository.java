package com.example.communiverse.repository;

import com.example.communiverse.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);

    List<User> findByUsernameRegex(String usernamePattern);

    List<User> findByMemberCommunitiesContainingOrModeratedCommunitiesContainingOrCreatedCommunitiesContaining(String communityId, String communityId2, String communityId3);

}
