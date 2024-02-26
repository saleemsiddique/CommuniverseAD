package com.example.communiverse.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;

    @NotBlank
    @Size(max = 20)
    private String username;

    @Size(max = 255)
    private String biography;

    @NotNull
    private int level;

    private List<String> createdCommunities;
    private List<String> moderatedCommunities;
    private List<String> memberCommunities;

    @NotNull
    private UserInteractions interactions;

    public User(String id, String name, String lastName, String email, String password, String username) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.username = username;
        this.biography = "";
        this.level = 0;
        this.createdCommunities = new ArrayList<>();
        this.moderatedCommunities = new ArrayList<>();
        this.memberCommunities = new ArrayList<>();
        this.interactions = new UserInteractions();
    }
}
