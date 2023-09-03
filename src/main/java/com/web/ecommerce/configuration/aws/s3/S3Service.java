package com.web.ecommerce.configuration.aws.s3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {
    private final S3Client s3Client;
    private final S3Buckets buckets;

    @Autowired
    public S3Service(S3Client s3Client, S3Buckets buckets) {
        this.s3Client = s3Client;
        this.buckets = buckets;
    }

    public void putObject(String key, byte[] file){
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(buckets.getCustomer())
                .key(key)
                .build();
        s3Client.putObject(objectRequest, RequestBody.fromBytes(file));
    }

    public void deleteObject(String key){
        DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                .bucket(buckets.getCustomer())
                .key(key)
                .build();
        s3Client.deleteObject(objectRequest);
    }

}
