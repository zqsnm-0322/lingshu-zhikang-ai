package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.service.ImageAIDiagnosisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 图像AI诊断控制器
 * 提供基于图像的健康诊断接口
 */
@RestController
@RequestMapping("/api/image-diagnosis")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ImageAIDiagnosisController {

    @Autowired
    private ImageAIDiagnosisService imageAIDiagnosisService;

    /**
     * 图像健康诊断接口
     * 
     * @param file 上传的图像文件（人像照片）
     * @return 诊断结果
     */
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeImage(
            @RequestParam("file") MultipartFile file) {
        
        System.out.println("=== 收到图像AI诊断请求 ===");
        System.out.println("文件名: " + file.getOriginalFilename());
        System.out.println("文件大小: " + file.getSize() + " bytes");
        System.out.println("文件类型: " + file.getContentType());
        
        try {
            // 1. 验证文件
            if (file.isEmpty()) {
                throw new RuntimeException("上传的文件为空");
            }
            
            if (!isImageFile(file)) {
                throw new RuntimeException("请上传图片文件（jpg/jpeg/png）");
            }
            
            // 2. 获取图像字节
            byte[] imageBytes = file.getBytes();
            System.out.println("✓ 图像读取成功");
            
            // 3. AI诊断
            System.out.println("开始AI图像分析...");
            Map<String, Object> diagnosis = imageAIDiagnosisService.diagnoseFromImage(imageBytes);
            System.out.println("✓ AI诊断完成");
            
            // 4. 返回结果
            Map<String, Object> response = imageAIDiagnosisService.formatDiagnosisResult(diagnosis);
            System.out.println("=== 图像AI诊断完成 ===");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ 图像诊断失败: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "图像分析服务暂不可用，请检查多模态模型配置后重试");
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量图像诊断（可选功能）
     * 
     * @param files 多个图像文件
     * @return 批量诊断结果
     */
    @PostMapping("/analyze-batch")
    public ResponseEntity<Map<String, Object>> analyzeBatch(
            @RequestParam("files") MultipartFile[] files) {
        
        System.out.println("=== 收到批量图像AI诊断请求 ===");
        System.out.println("文件数量: " + files.length);
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total", files.length);
            
            // 这里可以实现批量诊断逻辑
            // 暂时返回提示信息
            response.put("message", "批量诊断功能开发中");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "批量诊断失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "Image AI Diagnosis Service");
        response.put("message", "图像AI诊断服务运行正常");
        return ResponseEntity.ok(response);
    }

    /**
     * 验证是否为图片文件
     */
    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        
        return contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/gif");
    }
}
