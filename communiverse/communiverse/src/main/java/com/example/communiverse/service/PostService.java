package com.example.communiverse.service;

import com.example.communiverse.domain.Post;
import com.example.communiverse.domain.User;
import com.example.communiverse.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostService {

    Optional<Post> findById(String id);

    List<Post> findByAuthor_IdAndIsCommentFalseOrderByDateTimeDesc(String id, int page, int size);

    List<Post> findAllByRepostUserIdPaged(String repostUserId, int page, int size);

    List<Post> getCommentsByPostId(String postId, int page, int size);

    List<Post> findAllByCommunityIdOrderByInteractionsDesc(String communityId, int page, int size);

    List<Post> findAllWithQuizzOrderByInteractionsDesc(String communityId, int page, int size);

    List<Post> findPostsByCommunityAndFollowedUsers(String communityId, List<String> followedUsersIds, int page, int size);

    Post addPost(Post post);
}
