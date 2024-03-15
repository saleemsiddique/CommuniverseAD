package com.example.communiverse.domain;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quizz {

    @NotNull
    private String id;

    @NotNull
    @Size(min = 3, max = 20)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String description;

    private List<Question> questions;
}
