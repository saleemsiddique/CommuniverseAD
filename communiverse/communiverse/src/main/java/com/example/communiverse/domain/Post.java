package com.example.communiverse.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
public class Post {
    @Id
    private String id;

    @NotNull
    private Community community;

    @NotBlank
    private String author_id;

    private String content;

    private List<String> photos;

    private List<String> videos;

    private PostInteractions postInteractions;

    private User repost_usuario_id;

    private Quizz quizz;

}