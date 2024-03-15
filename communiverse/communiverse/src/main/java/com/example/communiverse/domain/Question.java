package com.example.communiverse.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @NotBlank
    @Size(min = 3, max = 60)
    private String question;

    private List<String> options;

    @NotBlank
    private String correct_answer;
}
