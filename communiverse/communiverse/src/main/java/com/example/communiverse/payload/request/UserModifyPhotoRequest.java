package com.example.communiverse.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModifyPhotoRequest {
    private InputStream photo;
}
