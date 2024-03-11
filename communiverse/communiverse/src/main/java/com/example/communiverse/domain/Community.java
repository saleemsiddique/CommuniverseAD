package com.example.communiverse.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "communities")
public class Community {
    @Id
    private String id;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    private String userCreator_id;

    @NotBlank
    @Size(max = 255)
    private String description;

    @NotBlank
    private EPrivacy privacy;

    @NotBlank
    private String photo;

    @NotNull
    private int followers;

    @NotNull
    private List<String> posts_id;
}
