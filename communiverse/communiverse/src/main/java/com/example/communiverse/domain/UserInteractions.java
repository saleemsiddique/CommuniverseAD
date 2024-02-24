package com.example.communiverse.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInteractions {
    @NotNull
    private int receivedLikes;

    @NotNull
    private int receivedReposts;
}
