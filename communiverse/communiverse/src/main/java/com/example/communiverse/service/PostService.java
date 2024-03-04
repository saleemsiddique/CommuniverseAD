package com.example.communiverse.service;

import com.example.communiverse.domain.Post;
import com.example.communiverse.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public interface PostService {

    Optional<Post> findById(String id);
    Post addPost(Post post);
}
