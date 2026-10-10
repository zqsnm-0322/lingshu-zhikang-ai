package com.lingshu.zhikang.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VideoProcessingService {

    private static final String UPLOAD_DIR = "uploads/";
    private static final String OUTPUT_DIR = "outputs/";

    public VideoProcessingService() {
        // 创建必要的目录
        // 注释掉自动创建目录的代码
        /*
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            Files.createDirectories(Paths.get(OUTPUT_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    /**
     * 将视频转换为灰度
     */
    public String convertVideoToGrayscale(MultipartFile file) throws IOException {
        System.out.println("=== 开始视频灰度转换 ===");
        System.out.println("文件名: " + file.getOriginalFilename());
        System.out.println("文件大小: " + file.getSize() + " bytes");

        // 按需创建输出目录
        Files.createDirectories(Paths.get(OUTPUT_DIR));
        
        String inputPath = saveUploadedFile(file);
        String outputFilename = "grayscale_video_" + System.currentTimeMillis() + ".mp4";
        String outputPath = OUTPUT_DIR + outputFilename;

        VideoCapture capture = new VideoCapture(inputPath);
        if (!capture.isOpened()) {
            throw new RuntimeException("无法打开视频文件");
        }

        // 获取视频属性
        double fps = capture.get(Videoio.CAP_PROP_FPS);
        int frameWidth = (int) capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        int frameHeight = (int) capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        int totalFrames = (int) capture.get(Videoio.CAP_PROP_FRAME_COUNT);

        System.out.println("视频信息 - FPS: " + fps + ", 尺寸: " + frameWidth + "x" + frameHeight + ", 总帧数: " + totalFrames);

        // 创建视频写入器
        VideoWriter writer = new VideoWriter(outputPath, VideoWriter.fourcc('m', 'p', '4', 'v'), fps, new Size(frameWidth, frameHeight), false);
        if (!writer.isOpened()) {
            throw new RuntimeException("无法创建输出视频文件");
        }

        Mat frame = new Mat();
        Mat grayFrame = new Mat();
        int processedFrames = 0;

        try {
            while (capture.read(frame)) {
                if (frame.empty()) break;

                // 转换为灰度
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
                
                // 写入帧
                writer.write(grayFrame);
                processedFrames++;

                if (processedFrames % 30 == 0) {
                    System.out.println("已处理帧数: " + processedFrames + "/" + totalFrames);
                }
            }
        } finally {
            capture.release();
            writer.release();
        }

        System.out.println("✓ 视频灰度转换完成，处理了 " + processedFrames + " 帧");
        return convertVideoToBase64(outputPath);
    }

    /**
     * 视频边缘检测
     */
    public String detectVideoEdges(MultipartFile file, double threshold1, double threshold2) throws IOException {
        System.out.println("=== 开始视频边缘检测 ===");
        System.out.println("阈值1: " + threshold1 + ", 阈值2: " + threshold2);

        // 按需创建输出目录
        Files.createDirectories(Paths.get(OUTPUT_DIR));
        
        String inputPath = saveUploadedFile(file);
        String outputFilename = "edges_video_" + System.currentTimeMillis() + ".mp4";
        String outputPath = OUTPUT_DIR + outputFilename;

        VideoCapture capture = new VideoCapture(inputPath);
        if (!capture.isOpened()) {
            throw new RuntimeException("无法打开视频文件");
        }

        double fps = capture.get(Videoio.CAP_PROP_FPS);
        int frameWidth = (int) capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        int frameHeight = (int) capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        int totalFrames = (int) capture.get(Videoio.CAP_PROP_FRAME_COUNT);

        VideoWriter writer = new VideoWriter(outputPath, VideoWriter.fourcc('m', 'p', '4', 'v'), fps, new Size(frameWidth, frameHeight), false);
        if (!writer.isOpened()) {
            throw new RuntimeException("无法创建输出视频文件");
        }

        Mat frame = new Mat();
        Mat grayFrame = new Mat();
        Mat edgeFrame = new Mat();
        int processedFrames = 0;

        try {
            while (capture.read(frame)) {
                if (frame.empty()) break;

                // 转换为灰度
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
                
                // 边缘检测
                Imgproc.Canny(grayFrame, edgeFrame, threshold1, threshold2);
                
                writer.write(edgeFrame);
                processedFrames++;

                if (processedFrames % 30 == 0) {
                    System.out.println("已处理帧数: " + processedFrames + "/" + totalFrames);
                }
            }
        } finally {
            capture.release();
            writer.release();
        }

        System.out.println("✓ 视频边缘检测完成");
        return convertVideoToBase64(outputPath);
    }

    /**
     * 视频高斯模糊
     */
    public String applyVideoGaussianBlur(MultipartFile file, int kernelSize, double sigmaX) throws IOException {
        System.out.println("=== 开始视频高斯模糊 ===");
        System.out.println("核大小: " + kernelSize + ", sigmaX: " + sigmaX);

        // 按需创建输出目录
        Files.createDirectories(Paths.get(OUTPUT_DIR));
        
        String inputPath = saveUploadedFile(file);
        String outputFilename = "blur_video_" + System.currentTimeMillis() + ".mp4";
        String outputPath = OUTPUT_DIR + outputFilename;

        VideoCapture capture = new VideoCapture(inputPath);
        if (!capture.isOpened()) {
            throw new RuntimeException("无法打开视频文件");
        }

        double fps = capture.get(Videoio.CAP_PROP_FPS);
        int frameWidth = (int) capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        int frameHeight = (int) capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        int totalFrames = (int) capture.get(Videoio.CAP_PROP_FRAME_COUNT);

        VideoWriter writer = new VideoWriter(outputPath, VideoWriter.fourcc('m', 'p', '4', 'v'), fps, new Size(frameWidth, frameHeight), true);
        if (!writer.isOpened()) {
            throw new RuntimeException("无法创建输出视频文件");
        }

        Mat frame = new Mat();
        Mat blurFrame = new Mat();
        int processedFrames = 0;

        try {
            while (capture.read(frame)) {
                if (frame.empty()) break;

                // 高斯模糊
                Size kSize = new Size(kernelSize, kernelSize);
                Imgproc.GaussianBlur(frame, blurFrame, kSize, sigmaX);
                
                writer.write(blurFrame);
                processedFrames++;

                if (processedFrames % 30 == 0) {
                    System.out.println("已处理帧数: " + processedFrames + "/" + totalFrames);
                }
            }
        } finally {
            capture.release();
            writer.release();
        }

        System.out.println("✓ 视频高斯模糊完成");
        return convertVideoToBase64(outputPath);
    }

    /**
     * 视频二值化
     */
    public String binarizeVideo(MultipartFile file, double threshold) throws IOException {
        System.out.println("=== 开始视频二值化 ===");
        System.out.println("阈值: " + threshold);

        // 按需创建输出目录
        Files.createDirectories(Paths.get(OUTPUT_DIR));
        
        String inputPath = saveUploadedFile(file);
        String outputFilename = "binary_video_" + System.currentTimeMillis() + ".mp4";
        String outputPath = OUTPUT_DIR + outputFilename;

        VideoCapture capture = new VideoCapture(inputPath);
        if (!capture.isOpened()) {
            throw new RuntimeException("无法打开视频文件");
        }

        double fps = capture.get(Videoio.CAP_PROP_FPS);
        int frameWidth = (int) capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        int frameHeight = (int) capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        int totalFrames = (int) capture.get(Videoio.CAP_PROP_FRAME_COUNT);

        VideoWriter writer = new VideoWriter(outputPath, VideoWriter.fourcc('m', 'p', '4', 'v'), fps, new Size(frameWidth, frameHeight), false);
        if (!writer.isOpened()) {
            throw new RuntimeException("无法创建输出视频文件");
        }

        Mat frame = new Mat();
        Mat grayFrame = new Mat();
        Mat binaryFrame = new Mat();
        int processedFrames = 0;

        try {
            while (capture.read(frame)) {
                if (frame.empty()) break;

                // 转换为灰度
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
                
                // 二值化
                Imgproc.threshold(grayFrame, binaryFrame, threshold, 255, Imgproc.THRESH_BINARY);
                
                writer.write(binaryFrame);
                processedFrames++;

                if (processedFrames % 30 == 0) {
                    System.out.println("已处理帧数: " + processedFrames + "/" + totalFrames);
                }
            }
        } finally {
            capture.release();
            writer.release();
        }

        System.out.println("✓ 视频二值化完成");
        return convertVideoToBase64(outputPath);
    }

    /**
     * 获取视频信息
     */
    public String getVideoInfo(MultipartFile file) throws IOException {
        String inputPath = saveUploadedFile(file);
        
        VideoCapture capture = new VideoCapture(inputPath);
        if (!capture.isOpened()) {
            throw new RuntimeException("无法打开视频文件");
        }

        try {
            double fps = capture.get(Videoio.CAP_PROP_FPS);
            int frameWidth = (int) capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
            int frameHeight = (int) capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
            int totalFrames = (int) capture.get(Videoio.CAP_PROP_FRAME_COUNT);
            double duration = totalFrames / fps;

            return String.format("视频信息 - 尺寸: %dx%d, FPS: %.2f, 总帧数: %d, 时长: %.2f秒", 
                               frameWidth, frameHeight, fps, totalFrames, duration);
        } finally {
            capture.release();
        }
    }

    /**
     * 保存上传的文件
     */
    private String saveUploadedFile(MultipartFile file) throws IOException {
        // 按需创建目录
        Files.createDirectories(Paths.get(UPLOAD_DIR));
        
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String filename = System.currentTimeMillis() + extension;
        Path filePath = Paths.get(UPLOAD_DIR + filename);
        Files.write(filePath, file.getBytes());
        
        String absolutePath = filePath.toAbsolutePath().toString().replace("\\", "/");
        System.out.println("视频文件保存到: " + absolutePath);
        
        return absolutePath;
    }

    /**
     * 将视频转换为Base64字符串
     */
    private String convertVideoToBase64(String videoPath) throws IOException {
        byte[] videoBytes = Files.readAllBytes(Paths.get(videoPath));
        return Base64.getEncoder().encodeToString(videoBytes);
    }
}