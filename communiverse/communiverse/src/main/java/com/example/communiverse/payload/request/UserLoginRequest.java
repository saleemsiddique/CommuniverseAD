package com.example.communiverse.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private boolean isGoogle;


    public UserLoginRequest(String emailOrUsername, String password, boolean isGoogle) {
        this.emailOrUsername = emailOrUsername;
        this.password = password;
        this.isGoogle = isGoogle;
    }
}
