package com.lingshu.zhikang.config;

import org.opencv.core.Core;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class OpenCVConfig {

    @PostConstruct
    public void initOpenCV() {
        try {
            // 使用正确的 OpenCV 加载方式 - openpnp 版本
            nu.pattern.OpenCV.loadShared();
            System.out.println("OpenCV 库加载成功，版本: " + Core.VERSION);
        } catch (Exception e) {
            System.err.println("OpenCV 库加载失败: " + e.getMessage());
            e.printStackTrace();
            // 尝试备用加载方式
            try {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                System.out.println("OpenCV 库通过备用方式加载成功，版本: " + Core.VERSION);
            } catch (Exception ex) {
                System.err.println("OpenCV 库备用加载也失败: " + ex.getMessage());
                // 不抛出异常，让应用继续启动，但会在处理时报错
                System.err.println("警告：OpenCV 库未能正确加载，图像处理功能可能不可用");
            }
        }
    }
}