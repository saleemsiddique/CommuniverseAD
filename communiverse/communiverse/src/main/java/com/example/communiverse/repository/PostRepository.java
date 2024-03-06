package com.example.communiverse.repository;

import com.example.communiverse.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    @Query("{'author_id' : ?0}")
    Page<Post> findByAuthor_IdOrderByDateTimeDesc(String id, Pageable pageable);

    @Query("{'repost_user_id' : ?0}")
    Page<Post> findAllByRepostUserIdPaged(String repostUserId, Pageable pageable);

    Optional<Post> findById(String id);
}
