package com.example.communiverse.service;

import com.example.communiverse.domain.Post;
import com.example.communiverse.repository.PostRepository;
import com.example.communiverse.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService{

    @Autowired
    private PostRepository postRepository;

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
        return postPage.getContent();
    }

    @Override
    public List<Post> getCommentsByPostId(String postId, int page, int size) {
        Post post = postRepository.findPostById(postId);
        List<Post> result = new ArrayList<>();
        if (post != null && post.getPostInteractions() != null) {
            List<String> commentIds = post.getPostInteractions().getComments_id();
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
    public Post addPost(Post post) {
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

        return postRepository.save(post);
    }

}
