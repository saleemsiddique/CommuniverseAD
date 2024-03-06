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

    Page<Post> findByAuthor_IdOrderByDateTimeDesc(String id, Pageable pageable);

    Page<Post> findAllByRepostUserIdPaged(String repostUserId, Pageable pageable);

    Post addPost(Post post);
}
