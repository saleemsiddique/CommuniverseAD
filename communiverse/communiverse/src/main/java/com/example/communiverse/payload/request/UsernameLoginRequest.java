package com.example.communiverse.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@NoArgsConstructor
@Getter
@ToString
public class UsernameLoginRequest {
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 30)
    private String username;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    public UsernameLoginRequest(String emailOrUsername, String password) {
        if (emailOrUsername.contains("@")) {
            this.email = emailOrUsername;
        } else {
            this.username = emailOrUsername;
        }
        this.password = password;
    }
}
