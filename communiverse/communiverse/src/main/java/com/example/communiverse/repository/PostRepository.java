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
    @Query(value = "{'author_id' : ?0, 'isComment' : {$ne : true}}", sort = "{'dateTime' : -1}")
    Page<Post> findByAuthor_IdAndIsCommentFalseOrderByDateTimeDesc(String id, Pageable pageable);

    @Query("{'repost_user_id' : ?0}")
    Page<Post> findAllByRepostUserIdPaged(String repostUserId, Pageable pageable);

    @Query("{'id' : ?0}")
    Post findPostById(String postId);

    @Query(value = "[{ $match: { 'community_id' : ?0 } }, { $addFields: { 'totalInteractions': { $sum: ['$postInteractions.likes', '$postInteractions.reposts', { $size: '$postInteractions.comments_id' }] } } }, { $sort: { 'totalInteractions': -1 } }]")
    Page<Post> findAllByCommunityIdOrderByInteractionsDesc(String communityId, Pageable pageable);

    @Query(value = "{ 'community_id' : ?0, 'quizz.questions': { $exists: true, $not: { $size: 0 } } }, { $addFields: { 'totalInteractions': { $sum: ['$postInteractions.likes', '$postInteractions.reposts', { $size: '$postInteractions.comments_id' }] } } }, { $sort: { 'totalInteractions': -1 } }")
    Page<Post> findAllWithQuizzOrderByInteractionsDesc(String communityId, Pageable pageable);

}