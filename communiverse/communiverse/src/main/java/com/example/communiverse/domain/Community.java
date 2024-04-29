package com.example.communiverse.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "communities")
public class Community {
    @Id
    private String id;

    @NotBlank
    @Size(max = 30)
    private String name;

    private String userCreator_id;

    @NotBlank
    @Size(max = 200)
    private String description;

    private String photo;

    @NotNull
    private int followers;

    @NotNull
    private List<String> posts_id;

    private List<BannedUser> banned;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BannedUser {
        private String user_id;
        private LocalDate until;
    }
}
