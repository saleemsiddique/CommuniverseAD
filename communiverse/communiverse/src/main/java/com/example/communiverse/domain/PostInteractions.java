package com.example.communiverse.domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostInteractions {
    private List<String> like_users_id;
    private List<String> repost_users_id;
    private ArrayList<String> comments_id;
}
