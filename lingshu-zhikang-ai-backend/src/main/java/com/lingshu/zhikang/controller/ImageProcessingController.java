package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.service.ImageProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/image")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ImageProcessingController {

    @Autowired
    private ImageProcessingService imageProcessingService;

    @PostMapping("/grayscale")
    public ResponseEntity<Map<String, Object>> convertToGrayscale(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("收到灰度转换请求，文件名: " + file.getOriginalFilename());
            System.out.println("文件大小: " + file.getSize() + " bytes");
            System.out.println("文件类型: " + file.getContentType());
            
            if (file.isEmpty()) {
                throw new RuntimeException("上传的文件为空");
            }
            
            String base64Image = imageProcessingService.convertToGrayscale(file);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("image", "data:image/jpeg;base64," + base64Image);
            response.put("message", "图像已成功转换为灰度");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("灰度转换失败: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "处理失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/edges")
    public ResponseEntity<Map<String, Object>> detectEdges(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "threshold1", defaultValue = "100") double threshold1,
            @RequestParam(value = "threshold2", defaultValue = "200") double threshold2) {
        try {
            String base64Image = imageProcessingService.detectEdges(file, threshold1, threshold2);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("image", "data:image/jpeg;base64," + base64Image);
            response.put("message", "边缘检测完成");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "处理失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/blur")
    public ResponseEntity<Map<String, Object>> applyGaussianBlur(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "kernelSize", defaultValue = "15") int kernelSize,
            @RequestParam(value = "sigmaX", defaultValue = "0") double sigmaX) {
        try {
            // 确保kernelSize是奇数
            if (kernelSize % 2 == 0) {
                kernelSize += 1;
            }
            String base64Image = imageProcessingService.applyGaussianBlur(file, kernelSize, sigmaX);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("image", "data:image/jpeg;base64," + base64Image);
            response.put("message", "高斯模糊处理完成");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "处理失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/binary")
    public ResponseEntity<Map<String, Object>> binarizeImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "threshold", defaultValue = "127") double threshold) {
        try {
            String base64Image = imageProcessingService.binarizeImage(file, threshold);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("image", "data:image/jpeg;base64," + base64Image);
            response.put("message", "图像二值化完成");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "处理失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "OpenCV 服务运行正常");
        return ResponseEntity.ok(response);
    }

    // ========== MinIO 版本接口 ==========

    @PostMapping("/grayscale/minio")
    public ResponseEntity<Map<String, Object>> convertToGrayscaleWithMinio(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("收到灰度转换请求 (MinIO版本)，文件名: " + file.getOriginalFilename());
            
            if (file.isEmpty()) {
                throw new RuntimeException("上传的文件为空");
            }
            
            String imageUrl = imageProcessingService.convertToGrayscaleWithMinio(file);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("imageUrl", imageUrl);
            response.put("message", "图像已成功转换为灰度并保存到 MinIO");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("灰度转换失败: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "处理失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/edges/minio")
    public ResponseEntity<Map<String, Object>> detectEdgesWithMinio(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "threshold1", defaultValue = "100") double threshold1,
            @RequestParam(value = "threshold2", defaultValue = "200") double threshold2) {
        try {
            String imageUrl = imageProcessingService.detectEdgesWithMinio(file, threshold1, threshold2);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("imageUrl", imageUrl);
            response.put("message", "边缘检测完成并保存到 MinIO");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "处理失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/blur/minio")
    public ResponseEntity<Map<String, Object>> applyGaussianBlurWithMinio(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "kernelSize", defaultValue = "15") int kernelSize,
            @RequestParam(value = "sigmaX", defaultValue = "0") double sigmaX) {
        try {
            if (kernelSize % 2 == 0) {
                kernelSize += 1;
            }
            String imageUrl = imageProcessingService.applyGaussianBlurWithMinio(file, kernelSize, sigmaX);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("imageUrl", imageUrl);
            response.put("message", "高斯模糊处理完成并保存到 MinIO");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "处理失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/binary/minio")
    public ResponseEntity<Map<String, Object>> binarizeImageWithMinio(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "threshold", defaultValue = "127") double threshold) {
        try {
            String imageUrl = imageProcessingService.binarizeImageWithMinio(file, threshold);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("imageUrl", imageUrl);
            response.put("message", "图像二值化完成并保存到 MinIO");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "处理失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}