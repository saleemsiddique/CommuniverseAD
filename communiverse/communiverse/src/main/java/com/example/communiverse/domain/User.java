package com.example.communiverse.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.persistence.*;
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
    @Size(min = 3, max = 50)
    private String name;

    @NotBlank
    @Size(min = 3, max = 50)
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @Lob
    private String photo;

    @Size(max = 50)
    private String biography;

    @NotNull
    private UserStats userStats;

    private List<String> createdCommunities;
    private List<String> moderatedCommunities;
    private List<String> memberCommunities;

    @NotNull
    private UserInteractions interactions;

    @NotNull
    private List<String> followers_id;

    @NotNull
    private List<String> followed_id;

    public User(String name, String lastName, String email, String password, String username) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.username = username;
        this.photo = "";
        this.biography = "";
        this.userStats = new UserStats();
        this.createdCommunities = new ArrayList<>();
        this.moderatedCommunities = new ArrayList<>();
        this.memberCommunities = new ArrayList<>();
        this.interactions = new UserInteractions();
        this.followers_id = new ArrayList<>();
        this.followed_id = new ArrayList<>();
    }
}
