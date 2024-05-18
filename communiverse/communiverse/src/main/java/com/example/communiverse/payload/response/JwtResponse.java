package com.example.communiverse.payload.response;

import lombok.*;

import java.util.List;

@Data
@Getter
@ToString
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String id;
    private String name;
    private String lastname;
    private String email;
    private String password;
    private String username;
    private boolean isGoogle;


    public JwtResponse(String token, String id, String name, String lastname, String email, String password, String username, boolean isGoogle) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.username = username;
        this.isGoogle = isGoogle;
    }
}