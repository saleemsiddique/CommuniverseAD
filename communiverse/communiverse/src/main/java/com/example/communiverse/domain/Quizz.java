package com.example.communiverse.domain;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quizz {

    @NotNull
    private String _id;

    @NotBlank
    private String description;

    private List<Question> questions;
}
