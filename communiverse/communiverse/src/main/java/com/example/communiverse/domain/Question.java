package com.example.communiverse.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @NotBlank
    private String question;

    private List<String> options;

    @NotBlank
    private String correct_answer;
}
