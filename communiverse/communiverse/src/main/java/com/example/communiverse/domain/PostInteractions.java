package com.example.communiverse.domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostInteractions {
    private int likes;
    private int reposts;
    private ArrayList<String> comments_id;
}
