package com.example.communiverse.service;

import com.azure.storage.blob.*;
import com.example.communiverse.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.Base64;
import java.util.UUID;
@Service
public class BlobStorageServiceImpl implements BlobStorageService {

    @Value("${azure.blob-storage.connection-string}")
    private String azureBlobConnectionString;

    @Value("${spring.cloud.azure.storage.blob-container-name}")
    private String blobContainerName;

    public String uploadPhoto(String urlBase64, String fileName) {
        System.out.println("LLEGO A UPLOAD");
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(azureBlobConnectionString)
                .buildClient();

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(blobContainerName);
        BlobClient blobClient = containerClient.getBlobClient(fileName);

        String cleanBase64 = urlBase64.replaceAll("[^A-Za-z0-9+/=]", "");
        byte[] imageBytes = Base64.getDecoder().decode(cleanBase64);
        InputStream imageInputStream = new ByteArrayInputStream(imageBytes);
        blobClient.upload(imageInputStream, imageBytes.length, true);
        System.out.println(blobClient.getProperties().getBlobType());

        String blobUrl = blobClient.getBlobUrl();
        System.out.println("Blob URL: " + blobUrl);
        return blobUrl;
    }

    public void deletePhoto(String blobUrl) {
        String blobName = blobUrl.substring(blobUrl.lastIndexOf('/') + 1);
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(azureBlobConnectionString)
                .buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(blobContainerName);
        containerClient.getBlobClient(blobName).delete();
    }

    public String getPhotoUrl(String fileName) {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(azureBlobConnectionString)
                .buildClient();

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(blobContainerName);
        BlobClient blobClient = containerClient.getBlobClient(fileName);

        return blobClient.getBlobUrl();
    }
}
