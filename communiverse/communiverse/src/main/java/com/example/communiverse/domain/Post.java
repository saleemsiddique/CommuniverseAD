package com.example.communiverse.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
public class Post {
    @Id
    private String id;

    @NotNull
    private String community_id;

    @NotBlank
    private String author_id;

    @Size(max = 200)
    private String content;

    private List<String> photos;

    private List<String> videos;

    private PostInteractions postInteractions;

    private Quizz quizz;

    private LocalDateTime dateTime;

    private boolean isComment;

    public void addCommentId(String commentId) {
        if (this.postInteractions == null) {
            this.postInteractions = new PostInteractions();
            this.postInteractions.setComments_id(new ArrayList<>());
        }
        this.postInteractions.getComments_id().add(commentId);
    }
}
