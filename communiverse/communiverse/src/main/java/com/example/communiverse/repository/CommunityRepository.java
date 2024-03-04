package com.example.communiverse.repository;

import com.example.communiverse.domain.Community;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityRepository extends MongoRepository<Community, String> {

    Optional<Community> findById(String id);
    List<Community> findTop5ByOrderByFollowersDesc();
}
