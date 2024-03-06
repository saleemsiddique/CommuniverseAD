package com.example.communiverse.service;

import com.example.communiverse.domain.Post;
import com.example.communiverse.repository.PostRepository;
import com.example.communiverse.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
    public Page<Post> findByAuthor_IdOrderByDateTimeDesc(String id, Pageable pageable) {
        return postRepository.findByAuthor_IdOrderByDateTimeDesc(id, pageable);
    }

    @Override
    public Page<Post> findAllByRepostUserIdPaged(String repostUserId, Pageable pageable) {
        return postRepository.findAllByRepostUserIdPaged(repostUserId, pageable);
    }

    @Override
    public Post addPost(Post post) {
        post.setId(IdGenerator.generateId());
        post.setDateTime(LocalDateTime.now());
        return postRepository.save(post);
    }
}
