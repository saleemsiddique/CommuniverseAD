package com.example.communiverse.payload.response;

import lombok.*;
@Data
@Getter
@ToString
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String contrasenya;
    private String username;


    public JwtResponse(String token, String type, Long id, String name, String lastname, String email, String contrasenya, String username) {
        this.token = token;
        this.type = type;
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.contrasenya = contrasenya;
        this.username = username;
    }
}