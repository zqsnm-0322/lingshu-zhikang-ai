package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.config.MinioConfig;
import com.lingshu.zhikang.entity.po.User;
import com.lingshu.zhikang.service.IUserService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private IUserService userService;
    
    @Autowired
    private MinioClient minioClient;
    
    @Autowired
    private MinioConfig minioConfig;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        System.out.println("接收到登录请求: " + user.getUsername());
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        Map<String, Object> result = new HashMap<>();
        if (loginUser != null) {
            result.put("code", "200");
            result.put("msg", "登录成功");
            result.put("data", loginUser);
        } else {
            result.put("code", "401");
            result.put("msg", "用户名或密码错误");
        }
        return result;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        System.out.println("接收到注册请求: " + user.getUsername());
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 参数验证
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                result.put("code", "400");
                result.put("msg", "用户名不能为空");
                return result;
            }
            
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                result.put("code", "400");
                result.put("msg", "密码不能为空");
                return result;
            }
            
            if (user.getUsername().length() < 3) {
                result.put("code", "400");
                result.put("msg", "用户名长度至少需要3个字符");
                return result;
            }
            
            if (user.getPassword().length() < 6) {
                result.put("code", "400");
                result.put("msg", "密码长度至少需要6个字符");
                return result;
            }
            
            // 检查用户名是否已存在
            if (userService.checkUsernameExists(user.getUsername())) {
                result.put("code", "409");
                result.put("msg", "用户名已存在");
                return result;
            }
            
            // 执行注册
            User registeredUser = userService.register(user);
            
            if (registeredUser != null) {
                result.put("code", "200");
                result.put("msg", "注册成功");
                result.put("data", registeredUser);
                System.out.println("用户注册成功: " + user.getUsername());
            } else {
                result.put("code", "500");
                result.put("msg", "注册失败，请稍后再试");
            }
            
        } catch (Exception e) {
            System.err.println("注册过程中发生错误: " + e.getMessage());
            e.printStackTrace();
            result.put("code", "500");
            result.put("msg", "注册失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 上传用户头像到MinIO
     */
    @PostMapping("/upload-avatar")
    public Map<String, Object> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            System.out.println("接收到头像上传请求，文件名: " + file.getOriginalFilename());
            
            // 验证文件
            if (file.isEmpty()) {
                result.put("code", "400");
                result.put("msg", "文件不能为空");
                return result;
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                result.put("code", "400");
                result.put("msg", "只能上传图片文件");
                return result;
            }
            
            // 验证文件大小（5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                result.put("code", "400");
                result.put("msg", "文件大小不能超过5MB");
                return result;
            }
            
            // 生成唯一的文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = "avatar_" + UUID.randomUUID().toString() + fileExtension;
            
            // 上传到MinIO
            byte[] fileBytes = file.getBytes();
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .stream(new ByteArrayInputStream(fileBytes), fileBytes.length, -1)
                    .contentType(contentType)
                    .build()
            );
            
            // 方案1：通过前端代理访问（推荐，避免CORS问题）
            String avatarUrl = "/minio/" + minioConfig.getBucketName() + "/" + fileName;
            
            // 方案2：直接URL（需要bucket设置为public，且前端COEP设置为unsafe-none）
            // String avatarUrl = minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + fileName;
            
            // 方案3：预签名URL（不需要public，但有7天有效期）
            // String avatarUrl = minioClient.getPresignedObjectUrl(
            //     GetPresignedObjectUrlArgs.builder()
            //         .method(Method.GET)
            //         .bucket(minioConfig.getBucketName())
            //         .object(fileName)
            //         .expiry(7, TimeUnit.DAYS)  // 7天有效期
            //         .build()
            // );
            
            System.out.println("✓ 头像上传成功: " + avatarUrl);
            System.out.println("📁 文件名: " + fileName);
            System.out.println("📦 Bucket: " + minioConfig.getBucketName());
            
            result.put("code", "200");
            result.put("msg", "头像上传成功");
            result.put("data", avatarUrl);
            
        } catch (Exception e) {
            System.err.println("❌ 头像上传失败: " + e.getMessage());
            e.printStackTrace();
            result.put("code", "500");
            result.put("msg", "头像上传失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取用户头像URL（预留接口，可以从数据库获取）
     */
    @GetMapping("/avatar")
    public Map<String, Object> getAvatar() {
        Map<String, Object> result = new HashMap<>();
        
        // TODO: 从数据库中获取用户头像URL
        // 目前暂时返回空，前端会使用localStorage缓存的URL
        
        result.put("code", "200");
        result.put("msg", "获取成功");
        result.put("data", null);
        
        return result;
    }
    
    /**
     * 获取用户信息（包括配额信息）
     */
    @GetMapping("/{userId}")
    public Map<String, Object> getUserInfo(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            System.out.println("=== 获取用户信息 ===");
            System.out.println("用户ID: " + userId);
            
            User user = userService.findById(userId);
            
            if (user == null) {
                result.put("code", "404");
                result.put("msg", "用户不存在");
                return result;
            }
            
            // 构建返回数据（不包含密码）
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("role", user.getRole());
            userData.put("memberLevel", user.getMemberLevel());
            userData.put("dailyQuota", user.getDailyQuota());
            userData.put("dailyUsed", user.getDailyUsed());
            userData.put("totalUsed", user.getTotalUsed());
            userData.put("lastUseDate", user.getLastUseDate());
            userData.put("status", user.getStatus());
            
            System.out.println("✓ 用户信息获取成功");
            System.out.println("会员等级: " + user.getMemberLevel());
            System.out.println("每日配额: " + user.getDailyQuota());
            System.out.println("今日已用: " + user.getDailyUsed());
            
            result.put("code", "200");
            result.put("msg", "获取成功");
            result.put("data", userData);
            
        } catch (Exception e) {
            System.err.println("❌ 获取用户信息失败: " + e.getMessage());
            e.printStackTrace();
            result.put("code", "500");
            result.put("msg", "获取用户信息失败: " + e.getMessage());
        }
        
        return result;
    }
} 