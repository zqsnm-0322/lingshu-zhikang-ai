package com.lingshu.zhikang.repository;

import com.lingshu.zhikang.config.MinioConfig;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.util.Objects;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class MinioFileRepository implements FileRepository {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Override
    public boolean save(String chatId, Resource resource) {
        try {
            // 确保 bucket 存在
            ensureBucketExists();

            // 获取文件名
            String filename = Objects.requireNonNull(resource.getFilename());
            
            // 根据文件扩展名确定内容类型
            String contentType = determineContentType(filename);
            
            // 上传文件到 MinIO
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(chatId)
                    .stream(resource.getInputStream(), resource.contentLength(), -1)
                    .contentType(contentType)
                    .build()
            );
            
            return true;
        } catch (Exception e) {
            log.error("Failed to save file to MinIO.", e);
            return false;
        }
    }

    @Override
    public Resource getFile(String chatId) {
        try {
            // 从 MinIO 获取文件
            InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(chatId)
                    .build()
            );

            // 将流转换为字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = stream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // 创建资源对象
            return new ByteArrayResource(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("Failed to get file from MinIO.", e);
            return null;
        }
    }

    private void ensureBucketExists() throws MinioException, IOException, InvalidKeyException {
        try {
            boolean found = minioClient.bucketExists(
                io.minio.BucketExistsArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .build()
            );
            
            if (!found) {
                minioClient.makeBucket(
                    io.minio.MakeBucketArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .build()
                );
            }
        } catch (Exception e) {
            log.error("Failed to ensure bucket exists.", e);
        }
    }

    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "pdf":
                return "application/pdf";
            case "txt":
                return "text/plain";
            case "doc":
            case "docx":
                return "application/msword";
            case "xls":
            case "xlsx":
                return "application/vnd.ms-excel";
            case "ppt":
            case "pptx":
                return "application/vnd.ms-powerpoint";
            default:
                return "application/octet-stream";
        }
    }
} 