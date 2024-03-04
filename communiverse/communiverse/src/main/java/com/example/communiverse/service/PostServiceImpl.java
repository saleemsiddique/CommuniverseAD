package com.example.communiverse.service;

import com.example.communiverse.domain.Post;
import com.example.communiverse.repository.PostRepository;
import com.example.communiverse.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostServiceImpl implements PostService{

    @Autowired
    private PostRepository postRepository;

    @Override
    public Optional<Post> findById(String id) {
        return postRepository.findById(id);
    }

    @Override
    public Post addPost(Post post) {
        post.setId(IdGenerator.generateId());
        return postRepository.save(post);
    }
}
