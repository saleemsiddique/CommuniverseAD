package com.example.communiverse.service;

import com.example.communiverse.domain.Post;
import com.example.communiverse.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public interface PostService {

    Optional<Post> findById(String id);

    List<Post> findByAuthor_IdOrderByDateTimeDesc(String id);

    Post addPost(Post post);
}
