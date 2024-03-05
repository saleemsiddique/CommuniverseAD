package com.example.communiverse.repository;

import com.example.communiverse.domain.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    @Query("{'author_id' : ?0}")
    List<Post> findByAuthor_IdOrderByDateTimeDesc(String id);
    @Query("{'repost_user_id' : ?0}")
    List<Post> findAllByRepostUserId(String repostUserId);

    Optional<Post> findById(String id);
}
