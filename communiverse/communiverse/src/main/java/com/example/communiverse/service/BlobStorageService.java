package com.example.communiverse.service;

import org.springframework.stereotype.Service;

import java.io.InputStream;
@Service
public interface BlobStorageService {

    String uploadPhoto(String urlBase64, String fileName);

    void deletePhoto(String blobUrl);

    String getPhotoUrl(String fileName);
}
