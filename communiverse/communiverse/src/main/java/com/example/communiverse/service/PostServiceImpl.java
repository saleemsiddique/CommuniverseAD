package com.example.communiverse.service;

import com.example.communiverse.domain.Post;
import com.example.communiverse.domain.PostInteractions;
import com.example.communiverse.domain.User;
import com.example.communiverse.repository.PostRepository;
import com.example.communiverse.repository.UserRepository;
import com.example.communiverse.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService{

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlobStorageService blobStorageService;

    @Override
    public Optional<Post> findById(String id) {
        return postRepository.findById(id);
    }

    @Override
    public List<Post> findByAuthor_IdAndIsCommentFalseOrderByDateTimeDesc(String id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findByAuthor_IdAndIsCommentFalseOrderByDateTimeDesc(id, pageable);
        return postPage.getContent();
    }

    @Override
    public List<Post> findAllByRepostUserIdPaged(String repostUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findAllByRepostUserIdPaged(repostUserId, pageable);
        System.out.println(postPage);
        return postPage.getContent();
    }

    @Override
    public List<Post> getCommentsByPostId(String postId, int page, int size) {
        Post post = postRepository.findPostById(postId);
        List<Post> result = new ArrayList<>();
        if (post != null && post.getPostInteractions() != null) {
            List<String> commentIds = post.getPostInteractions().getComments_id();
            // Ensure comments are sorted by date (most recent first)
            Collections.reverse(commentIds);
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, commentIds.size());
            for (int i = startIndex; i < endIndex; i++) {
                Post comment = postRepository.findPostById(commentIds.get(i));
                if (comment != null) {
                    result.add(comment);
                }
            }
        }
        return result;
    }

    @Override
    public List<Post> findAllByCommunityIdOrderByInteractionsDesc(String communityId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAllByCommunityIdOrderByInteractionsDesc(communityId, pageable).getContent();
    }

    @Override
    public List<Post> findAllWithQuizzOrderByInteractionsDesc(String communityId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAllWithQuizzOrderByInteractionsDesc(communityId, pageable).getContent();
    }

    @Override
    public List<Post> findPostsByCommunityAndFollowedUsers(String communityId, List<String> followedUsersIds, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findPostsByCommunityAndFollowedUsers(communityId, followedUsersIds, pageable).getContent();
    }

    @Override
    public Post addPost(Post post, String parentPostId) {
        post.setId(IdGenerator.generateId());
        post.setDateTime(LocalDateTime.now());

        // Subir fotos
        if (post.getPhotos() != null && !post.getPhotos().isEmpty()) {
            List<String> uploadedPhotos = new ArrayList<>();
            for (String photoBase64 : post.getPhotos()) {
                String photoUrl = blobStorageService.uploadPhoto(photoBase64, IdGenerator.generateId() + "-post_" + post.getId() + "-image.jpg");
                uploadedPhotos.add(photoUrl);
            }
            post.setPhotos(uploadedPhotos);
        }

        // Subir videos
        if (post.getVideos() != null && !post.getVideos().isEmpty()) {
            List<String> uploadedVideos = new ArrayList<>();
            for (String videoBase64 : post.getVideos()) {
                String videoUrl = blobStorageService.uploadPhoto(videoBase64, IdGenerator.generateId() + "-post_" + post.getId() + "-video.mp4");
                uploadedVideos.add(videoUrl);
            }
            post.setVideos(uploadedVideos);
        }

        if (!post.getQuizz().getQuestions().isEmpty()){
            post.getQuizz().setId(IdGenerator.generateId());
        }

        if (post.isComment() && parentPostId != null && !parentPostId.isEmpty()) {
            Post parentPost = postRepository.findById(parentPostId).orElse(null);
            if (parentPost != null) {
                parentPost.addCommentId(post.getId());
                Optional<User> userOp = userRepository.findById(parentPost.getAuthor_id());
                if (userOp.isPresent()) {
                    User user = userOp.get();
                    user.getUserStats().increasePoints(3);
                    userRepository.save(user);
                    postRepository.save(parentPost);
                }
            }
        }

        return postRepository.save(post);
    }

    @Override
    public Post addLike(Post post, String userId) {
        post.getPostInteractions().getLike_users_id().add(userId);
        Optional<User> userOp = userRepository.findById(post.getAuthor_id());
        if (userOp.isPresent()) {
            User user = userOp.get();
            user.getUserStats().increasePoints(1);
            userRepository.save(user);
        }
        return postRepository.save(post);
    }

    @Override
    public Post removeLike(Post post, String userId) {
        post.getPostInteractions().getLike_users_id().remove(userId);
        Optional<User> userOp = userRepository.findById(post.getAuthor_id());
        if (userOp.isPresent()) {
            User user = userOp.get();
            user.getUserStats().decreasePoints(1);
            userRepository.save(user);
        }
        return postRepository.save(post);
    }

    @Override
    public Post addRepost(Post post, String userId) {
        post.getPostInteractions().getRepost_users_id().add(userId);
        Optional<User> userOp = userRepository.findById(post.getAuthor_id());
        if (userOp.isPresent()) {
            User user = userOp.get();
            user.getUserStats().increasePoints(2);
            userRepository.save(user);
        }
        return postRepository.save(post);
    }

    @Override
    public Post removeRepost(Post post, String userId) {
        post.getPostInteractions().getRepost_users_id().remove(userId);
        Optional<User> userOp = userRepository.findById(post.getAuthor_id());
        if (userOp.isPresent()) {
            User user = userOp.get();
            user.getUserStats().decreasePoints(2);
            userRepository.save(user);
        }
        return postRepository.save(post);
    }


    @Override
    public Post deletePostById(String id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (post.isComment()) {
                // Si el post es un comentario, eliminamos su ID de la lista de comentarios
                Optional<Post> pPost = findPostByCommentId(id);
                if (pPost.isPresent()){
                    Post parentPost = pPost.get();
                    PostInteractions postInteractions = parentPost.getPostInteractions();
                    if (postInteractions != null && postInteractions.getComments_id() != null) {
                        parentPost.getPostInteractions().getComments_id().remove(id);
                    }
                    postRepository.save(parentPost);
                }
            }
            if (!post.getPhotos().isEmpty()){
                for (String blobname : post.getPhotos()){
                    blobStorageService.deletePhoto(blobname);
                }
            }
            if (!post.getVideos().isEmpty()) {
                for (String blobname : post.getVideos()) {
                    blobStorageService.deletePhoto(blobname);
                }
            }
            return postRepository.deletePostById(id);
        } else {
            // Manejar el caso en el que el post no existe
            throw new IllegalArgumentException("No se encontró ningún post con el ID: " + id);
        }
    }

    @Override
    public Optional<Post> findPostByCommentId(String commentId) {
        return postRepository.findByCommentId(commentId);
    }
}
