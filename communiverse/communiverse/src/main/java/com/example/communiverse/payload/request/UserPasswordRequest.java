package com.example.communiverse.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPasswordRequest {
    @NotBlank
    @Size(max = 50)
    private String emailOrUsername;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private String newPassword;
}
