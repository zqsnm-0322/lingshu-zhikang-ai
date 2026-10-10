package com.lingshu.zhikang.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lingshu.zhikang.entity.po.HealthReportMetadata;
import com.lingshu.zhikang.entity.vo.ResponseResult;
import com.lingshu.zhikang.mapper.HealthReportMetadataMapper;
import com.lingshu.zhikang.repository.FileRepository;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/health-report")
@RequiredArgsConstructor
public class HealthReportController {
    
    private final FileRepository fileRepository;
    private final MinioClient minioClient;
    private final HealthReportMetadataMapper reportMetadataMapper;
    
    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * 上传体检报告（支持PDF和图片）
     */
    @PostMapping("/upload")
    public ResponseResult<Map<String, String>> uploadReport(@RequestParam("file") MultipartFile file,
                                                             @RequestParam Long userId,
                                                             @RequestParam(required = false) String title) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return ResponseResult.error("文件不能为空");
            }

            String contentType = file.getContentType();
            if (contentType == null || 
                (!contentType.equals("application/pdf") && 
                 !contentType.startsWith("image/"))) {
                return ResponseResult.error("只支持PDF文件和图片格式");
            }

            // 生成唯一的文件ID
            String fileId = "health-report-" + UUID.randomUUID().toString();
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.contains(".")) {
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                fileId = fileId + extension;
            }

            // 保存到MinIO
            boolean success = fileRepository.save(fileId, file.getResource());
            if (!success) {
                return ResponseResult.error("文件保存失败");
            }

            // 生成预览URL（有效期7天）
            String previewUrl = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(fileId)
                    .expiry(7, TimeUnit.DAYS)
                    .build()
            );

            Map<String, String> result = new HashMap<>();
            result.put("fileId", fileId);
            result.put("fileName", originalFilename);
            result.put("previewUrl", previewUrl);
            result.put("contentType", contentType);

            HealthReportMetadata metadata = new HealthReportMetadata();
            metadata.setUserId(userId);
            metadata.setFileId(fileId);
            metadata.setFileName(originalFilename);
            metadata.setTitle(title == null || title.isBlank() ? originalFilename : title);
            metadata.setContentType(contentType);
            metadata.setFileSize(file.getSize());
            metadata.setCreatedTime(LocalDateTime.now());
            metadata.setUpdatedTime(LocalDateTime.now());
            reportMetadataMapper.insert(metadata);
            
            log.info("体检报告上传成功: fileId={}, fileName={}", fileId, originalFilename);
            return ResponseResult.success(result);
            
        } catch (Exception e) {
            log.error("上传体检报告失败", e);
            return ResponseResult.error("上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseResult<List<HealthReportMetadata>> listReports(@RequestParam Long userId) {
        return ResponseResult.success(reportMetadataMapper.selectList(
                new LambdaQueryWrapper<HealthReportMetadata>()
                        .eq(HealthReportMetadata::getUserId, userId)
                        .orderByDesc(HealthReportMetadata::getCreatedTime)));
    }

    @PutMapping("/{id}")
    public ResponseResult<HealthReportMetadata> updateReport(@PathVariable Long id,
                                                              @RequestParam Long userId,
                                                              @RequestBody Map<String, String> payload) {
        HealthReportMetadata metadata = reportMetadataMapper.selectOne(
                new LambdaQueryWrapper<HealthReportMetadata>()
                        .eq(HealthReportMetadata::getId, id)
                        .eq(HealthReportMetadata::getUserId, userId));
        if (metadata == null) return ResponseResult.error("报告不存在或无权限操作");
        metadata.setTitle(payload.getOrDefault("title", metadata.getTitle()));
        metadata.setUpdatedTime(LocalDateTime.now());
        reportMetadataMapper.updateById(metadata);
        return ResponseResult.success(metadata);
    }

    /**
     * 获取体检报告预览URL
     */
    @GetMapping("/preview/{fileId}")
    public ResponseResult<String> getPreviewUrl(@PathVariable String fileId) {
        try {
            // 生成预览URL（有效期24小时）
            String previewUrl = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(fileId)
                    .expiry(24, TimeUnit.HOURS)
                    .build()
            );
            
            return ResponseResult.success(previewUrl);
        } catch (Exception e) {
            log.error("获取预览URL失败", e);
            return ResponseResult.error("获取预览链接失败");
        }
    }

    /**
     * 下载体检报告
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadReport(@PathVariable String fileId) {
        try {
            var resource = fileRepository.getFile(fileId);
            if (resource == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + fileId + "\"")
                .body(resource.getContentAsByteArray());
                
        } catch (Exception e) {
            log.error("下载体检报告失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 删除体检报告
     */
    @DeleteMapping("/delete/{fileId}")
    public ResponseResult<Void> deleteReport(@PathVariable String fileId,
                                              @RequestParam(required = false) Long userId) {
        try {
            // 从MinIO删除文件
            minioClient.removeObject(
                io.minio.RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileId)
                    .build()
            );
            LambdaQueryWrapper<HealthReportMetadata> wrapper = new LambdaQueryWrapper<HealthReportMetadata>()
                    .eq(HealthReportMetadata::getFileId, fileId);
            if (userId != null) wrapper.eq(HealthReportMetadata::getUserId, userId);
            reportMetadataMapper.delete(wrapper);
            
            log.info("体检报告删除成功: fileId={}", fileId);
            return ResponseResult.success(null);
        } catch (Exception e) {
            log.error("删除体检报告失败", e);
            return ResponseResult.error("删除失败");
        }
    }
}

