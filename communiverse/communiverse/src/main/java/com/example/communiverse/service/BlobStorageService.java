package com.example.communiverse.service;

import org.springframework.stereotype.Service;

import java.io.InputStream;
@Service
public interface BlobStorageService {
    String uploadPhoto(InputStream inputStream, String fileName);

    void deletePhoto(String blobUrl);

    String getPhotoUrl(String fileName);
}
