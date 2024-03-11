package com.example.communiverse.service;

import com.azure.storage.blob.*;
import com.example.communiverse.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.UUID;
@Service
public class BlobStorageServiceImpl implements BlobStorageService {

    @Value("${azure.blob-storage.connection-string}")
    private String azureBlobConnectionString;

    @Value("${spring.cloud.azure.storage.blob-container-name}")
    private String blobContainerName;

    public String uploadPhoto(InputStream inputStream, String fileName) {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(azureBlobConnectionString)
                .buildClient();

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(blobContainerName);
        String blobName = IdGenerator.generateId() + "-" + fileName;
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        blobClient.upload(inputStream, -1, true);

        return blobClient.getBlobUrl();
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
