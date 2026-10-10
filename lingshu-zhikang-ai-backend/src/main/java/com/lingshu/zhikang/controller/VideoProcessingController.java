package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.service.VideoProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/video")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class VideoProcessingController {

    @Autowired
    private VideoProcessingService videoProcessingService;

    @PostMapping("/grayscale")
    public ResponseEntity<Map<String, Object>> convertToGrayscale(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("收到视频灰度转换请求，文件名: " + file.getOriginalFilename());
            System.out.println("文件大小: " + file.getSize() + " bytes");
            System.out.println("文件类型: " + file.getContentType());
            
            if (file.isEmpty()) {
                throw new RuntimeException("上传的文件为空");
            }
            
            String base64Video = videoProcessingService.convertVideoToGrayscale(file);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("video", "data:video/mp4;base64," + base64Video);
            response.put("message", "视频已成功转换为灰度");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("视频灰度转换失败: " + e.getMessage());
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
            String base64Video = videoProcessingService.detectVideoEdges(file, threshold1, threshold2);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("video", "data:video/mp4;base64," + base64Video);
            response.put("message", "视频边缘检测完成");
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
            String base64Video = videoProcessingService.applyVideoGaussianBlur(file, kernelSize, sigmaX);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("video", "data:video/mp4;base64," + base64Video);
            response.put("message", "视频高斯模糊处理完成");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "处理失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/binary")
    public ResponseEntity<Map<String, Object>> binarizeVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "threshold", defaultValue = "127") double threshold) {
        try {
            String base64Video = videoProcessingService.binarizeVideo(file, threshold);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("video", "data:video/mp4;base64," + base64Video);
            response.put("message", "视频二值化完成");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "处理失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/info")
    public ResponseEntity<Map<String, Object>> getVideoInfo(@RequestParam("file") MultipartFile file) {
        try {
            String info = videoProcessingService.getVideoInfo(file);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("info", info);
            response.put("message", "视频信息获取成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取视频信息失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "视频处理服务运行正常");
        return ResponseEntity.ok(response);
    }
}