package com.example.communiverse.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
@Data
@NoArgsConstructor
public class UserLoginRequest {
    @NotBlank
    @Size(max = 50)
    private String emailOrUsername;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    public UserLoginRequest(String emailOrUsername, String password) {
        this.emailOrUsername = emailOrUsername;
        this.password = password;
    }
}
