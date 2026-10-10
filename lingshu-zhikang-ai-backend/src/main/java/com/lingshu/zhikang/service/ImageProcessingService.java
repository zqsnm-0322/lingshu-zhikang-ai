package com.lingshu.zhikang.service;

import com.lingshu.zhikang.config.MinioConfig;
import com.lingshu.zhikang.repository.MinioFileRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class ImageProcessingService {

    private static final String UPLOAD_DIR = "uploads/";
    private static final String OUTPUT_DIR = "outputs/";

    @Autowired
    private MinioClient minioClient;
    
    @Autowired
    private MinioConfig minioConfig;
    
    @Autowired
    private MinioFileRepository minioFileRepository;

    public ImageProcessingService() {
        // 创建必要的目录（作为备用）
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
     * 将图像转换为灰度
     */
    public String convertToGrayscale(MultipartFile file) throws IOException {
        System.out.println("=== 开始处理灰度转换 ===");
        System.out.println("文件名: " + file.getOriginalFilename());
        System.out.println("文件大小: " + file.getSize() + " bytes");
        System.out.println("文件类型: " + file.getContentType());
        
        // 强制使用内存处理，不回退到文件处理
        byte[] imageBytes = file.getBytes();
        System.out.println("✓ 获取文件字节数组成功，大小: " + imageBytes.length);
        
        // 使用OpenCV的imdecode从内存中解码图像
        Mat imageData = new Mat(1, imageBytes.length, CvType.CV_8UC1);
        imageData.put(0, 0, imageBytes);
        System.out.println("✓ 创建Mat数据成功");
        
        Mat image = Imgcodecs.imdecode(imageData, Imgcodecs.IMREAD_COLOR);
        System.out.println("imdecode结果 - 图像是否为空: " + image.empty());
        
        if (image.empty()) {
            System.err.println("❌ 无法解码图像数据");
            throw new RuntimeException("无法解码图像数据，可能是格式不支持");
        }
        System.out.println("✓ 图像解码成功，尺寸: " + image.rows() + "x" + image.cols());
        
        // 转换为灰度
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        System.out.println("✓ 灰度转换完成");
        
        // 编码为JPEG格式的字节数组
        MatOfByte matOfByte = new MatOfByte();
        boolean success = Imgcodecs.imencode(".jpg", grayImage, matOfByte);
        System.out.println("imencode结果: " + success);
        
        if (!success) {
            throw new RuntimeException("无法编码处理后的图像");
        }
        
        byte[] encodedBytes = matOfByte.toArray();
        System.out.println("✓ 图像编码成功，输出大小: " + encodedBytes.length + " bytes");
        
        // 直接返回Base64编码
        String base64Result = Base64.getEncoder().encodeToString(encodedBytes);
        System.out.println("✓ Base64编码完成，长度: " + base64Result.length());
        System.out.println("=== 灰度转换处理完成 ===");
        
        return base64Result;
    }
    
    /**
     * 使用文件方式处理灰度转换（备用方法）
     */
    private String convertToGrayscaleWithFile(MultipartFile file) throws IOException {
        String inputPath = saveUploadedFile(file);
        System.out.println("文件已保存到: " + inputPath);
        
        String outputFilename = "grayscale_" + System.currentTimeMillis() + ".jpg";
        Path outputFilePath = Paths.get(OUTPUT_DIR + outputFilename);
        String outputPath = outputFilePath.toAbsolutePath().toString().replace("\\", "/");
        System.out.println("输出路径: " + outputPath);

        Mat image = Imgcodecs.imread(inputPath);
        if (image.empty()) {
            throw new RuntimeException("无法读取图像文件，可能是格式不支持或文件损坏");
        }
        System.out.println("图像读取成功，尺寸: " + image.rows() + "x" + image.cols());
        
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        System.out.println("灰度转换完成");
        
        boolean success = Imgcodecs.imwrite(outputPath, grayImage);
        if (!success) {
            throw new RuntimeException("无法保存处理后的图像");
        }
        System.out.println("图像保存成功");

        return convertImageToBase64(outputPath);
    }

    /**
     * 边缘检测 (Canny)
     */
    public String detectEdges(MultipartFile file, double threshold1, double threshold2) throws IOException {
        System.out.println("=== 开始边缘检测处理 ===");
        System.out.println("阈值1: " + threshold1 + ", 阈值2: " + threshold2);
        
        // 使用内存处理方式
        byte[] imageBytes = file.getBytes();
        System.out.println("✓ 获取文件字节数组成功，大小: " + imageBytes.length);
        
        // 解码图像
        Mat imageData = new Mat(1, imageBytes.length, CvType.CV_8UC1);
        imageData.put(0, 0, imageBytes);
        
        Mat image = Imgcodecs.imdecode(imageData, Imgcodecs.IMREAD_COLOR);
        if (image.empty()) {
            throw new RuntimeException("无法解码图像数据");
        }
        System.out.println("✓ 图像解码成功，尺寸: " + image.rows() + "x" + image.cols());
        
        // 转换为灰度
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        System.out.println("✓ 灰度转换完成");
        
        // Canny边缘检测
        Mat edges = new Mat();
        Imgproc.Canny(grayImage, edges, threshold1, threshold2);
        System.out.println("✓ 边缘检测完成");
        
        // 编码为JPEG
        MatOfByte matOfByte = new MatOfByte();
        boolean success = Imgcodecs.imencode(".jpg", edges, matOfByte);
        if (!success) {
            throw new RuntimeException("无法编码处理后的图像");
        }
        
        byte[] encodedBytes = matOfByte.toArray();
        System.out.println("✓ 图像编码成功，输出大小: " + encodedBytes.length + " bytes");
        
        String base64Result = Base64.getEncoder().encodeToString(encodedBytes);
        System.out.println("=== 边缘检测处理完成 ===");
        
        return base64Result;
    }

    /**
     * 高斯模糊
     */
    public String applyGaussianBlur(MultipartFile file, int kernelSize, double sigmaX) throws IOException {
        System.out.println("=== 开始高斯模糊处理 ===");
        System.out.println("核大小: " + kernelSize + ", sigmaX: " + sigmaX);
        
        // 使用内存处理方式
        byte[] imageBytes = file.getBytes();
        Mat imageData = new Mat(1, imageBytes.length, CvType.CV_8UC1);
        imageData.put(0, 0, imageBytes);
        
        Mat image = Imgcodecs.imdecode(imageData, Imgcodecs.IMREAD_COLOR);
        if (image.empty()) {
            throw new RuntimeException("无法解码图像数据");
        }
        System.out.println("✓ 图像解码成功");
        
        Mat blurredImage = new Mat();
        Size kSize = new Size(kernelSize, kernelSize);
        Imgproc.GaussianBlur(image, blurredImage, kSize, sigmaX);
        System.out.println("✓ 高斯模糊完成");
        
        // 编码为JPEG
        MatOfByte matOfByte = new MatOfByte();
        boolean success = Imgcodecs.imencode(".jpg", blurredImage, matOfByte);
        if (!success) {
            throw new RuntimeException("无法编码处理后的图像");
        }
        
        byte[] encodedBytes = matOfByte.toArray();
        String base64Result = Base64.getEncoder().encodeToString(encodedBytes);
        System.out.println("=== 高斯模糊处理完成 ===");
        
        return base64Result;
    }

    /**
     * 图像二值化
     */
    public String binarizeImage(MultipartFile file, double threshold) throws IOException {
        System.out.println("=== 开始二值化处理 ===");
        System.out.println("阈值: " + threshold);
        
        // 使用内存处理方式
        byte[] imageBytes = file.getBytes();
        Mat imageData = new Mat(1, imageBytes.length, CvType.CV_8UC1);
        imageData.put(0, 0, imageBytes);
        
        Mat image = Imgcodecs.imdecode(imageData, Imgcodecs.IMREAD_COLOR);
        if (image.empty()) {
            throw new RuntimeException("无法解码图像数据");
        }
        System.out.println("✓ 图像解码成功");
        
        Mat grayImage = new Mat();
        Mat binaryImage = new Mat();
        
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(grayImage, binaryImage, threshold, 255, Imgproc.THRESH_BINARY);
        System.out.println("✓ 二值化完成");
        
        // 编码为JPEG
        MatOfByte matOfByte = new MatOfByte();
        boolean success = Imgcodecs.imencode(".jpg", binaryImage, matOfByte);
        if (!success) {
            throw new RuntimeException("无法编码处理后的图像");
        }
        
        byte[] encodedBytes = matOfByte.toArray();
        String base64Result = Base64.getEncoder().encodeToString(encodedBytes);
        System.out.println("=== 二值化处理完成 ===");
        
        return base64Result;
    }

    /**
     * 保存上传的文件
     */
    private String saveUploadedFile(MultipartFile file) throws IOException {
        // 按需创建目录
        Files.createDirectories(Paths.get(UPLOAD_DIR));
        
        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // 使用时间戳作为文件名，避免中文字符问题
        String filename = System.currentTimeMillis() + extension;
        Path filePath = Paths.get(UPLOAD_DIR + filename);
        Files.write(filePath, file.getBytes());
        
        // 获取绝对路径并转换为正斜杠
        String absolutePath = filePath.toAbsolutePath().toString().replace("\\", "/");
        System.out.println("文件绝对路径: " + absolutePath);
        
        // 验证文件是否存在
        if (Files.exists(filePath)) {
            System.out.println("文件确实存在，大小: " + Files.size(filePath) + " bytes");
        } else {
            System.out.println("警告：文件不存在！");
        }
        
        return absolutePath;
    }

    /**
     * 将图像转换为Base64字符串
     */
    private String convertImageToBase64(String imagePath) throws IOException {
        byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * 将处理后的图像保存到 MinIO 并返回访问 URL
     */
    private String saveProcessedImageToMinio(byte[] imageBytes, String operation) {
        try {
            // 生成唯一的文件名
            String fileName = "processed_" + operation + "_" + System.currentTimeMillis() + ".jpg";
            
            // 上传到 MinIO
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .stream(new ByteArrayInputStream(imageBytes), imageBytes.length, -1)
                    .contentType("image/jpeg")
                    .build()
            );
            
            // 返回 MinIO 访问 URL
            String minioUrl = minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + fileName;
            System.out.println("✓ 图像已保存到 MinIO: " + minioUrl);
            return minioUrl;
            
        } catch (Exception e) {
            System.err.println("❌ 保存图像到 MinIO 失败: " + e.getMessage());
            e.printStackTrace();
            // 如果 MinIO 保存失败，回退到 Base64
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes);
        }
    }

    /**
     * 增强版灰度转换 - 支持 MinIO 存储
     */
    public String convertToGrayscaleWithMinio(MultipartFile file) throws IOException {
        System.out.println("=== 开始处理灰度转换 (MinIO版本) ===");
        
        // 使用内存处理方式
        byte[] imageBytes = file.getBytes();
        Mat imageData = new Mat(1, imageBytes.length, CvType.CV_8UC1);
        imageData.put(0, 0, imageBytes);
        
        Mat image = Imgcodecs.imdecode(imageData, Imgcodecs.IMREAD_COLOR);
        if (image.empty()) {
            throw new RuntimeException("无法解码图像数据");
        }
        
        // 转换为灰度
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        
        // 编码为JPEG
        MatOfByte matOfByte = new MatOfByte();
        boolean success = Imgcodecs.imencode(".jpg", grayImage, matOfByte);
        if (!success) {
            throw new RuntimeException("无法编码处理后的图像");
        }
        
        byte[] encodedBytes = matOfByte.toArray();
        
        // 保存到 MinIO 并返回 URL
        return saveProcessedImageToMinio(encodedBytes, "grayscale");
    }

    /**
     * 边缘检测 - MinIO 版本
     */
    public String detectEdgesWithMinio(MultipartFile file, double threshold1, double threshold2) throws IOException {
        System.out.println("=== 开始边缘检测处理 (MinIO版本) ===");
        
        byte[] imageBytes = file.getBytes();
        Mat imageData = new Mat(1, imageBytes.length, CvType.CV_8UC1);
        imageData.put(0, 0, imageBytes);
        
        Mat image = Imgcodecs.imdecode(imageData, Imgcodecs.IMREAD_COLOR);
        if (image.empty()) {
            throw new RuntimeException("无法解码图像数据");
        }
        
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        
        Mat edges = new Mat();
        Imgproc.Canny(grayImage, edges, threshold1, threshold2);
        
        MatOfByte matOfByte = new MatOfByte();
        boolean success = Imgcodecs.imencode(".jpg", edges, matOfByte);
        if (!success) {
            throw new RuntimeException("无法编码处理后的图像");
        }
        
        byte[] encodedBytes = matOfByte.toArray();
        return saveProcessedImageToMinio(encodedBytes, "edges");
    }

    /**
     * 高斯模糊 - MinIO 版本
     */
    public String applyGaussianBlurWithMinio(MultipartFile file, int kernelSize, double sigmaX) throws IOException {
        System.out.println("=== 开始高斯模糊处理 (MinIO版本) ===");
        
        byte[] imageBytes = file.getBytes();
        Mat imageData = new Mat(1, imageBytes.length, CvType.CV_8UC1);
        imageData.put(0, 0, imageBytes);
        
        Mat image = Imgcodecs.imdecode(imageData, Imgcodecs.IMREAD_COLOR);
        if (image.empty()) {
            throw new RuntimeException("无法解码图像数据");
        }
        
        Mat blurredImage = new Mat();
        Size kSize = new Size(kernelSize, kernelSize);
        Imgproc.GaussianBlur(image, blurredImage, kSize, sigmaX);
        
        MatOfByte matOfByte = new MatOfByte();
        boolean success = Imgcodecs.imencode(".jpg", blurredImage, matOfByte);
        if (!success) {
            throw new RuntimeException("无法编码处理后的图像");
        }
        
        byte[] encodedBytes = matOfByte.toArray();
        return saveProcessedImageToMinio(encodedBytes, "blur");
    }

    /**
     * 图像二值化 - MinIO 版本
     */
    public String binarizeImageWithMinio(MultipartFile file, double threshold) throws IOException {
        System.out.println("=== 开始二值化处理 (MinIO版本) ===");
        
        byte[] imageBytes = file.getBytes();
        Mat imageData = new Mat(1, imageBytes.length, CvType.CV_8UC1);
        imageData.put(0, 0, imageBytes);
        
        Mat image = Imgcodecs.imdecode(imageData, Imgcodecs.IMREAD_COLOR);
        if (image.empty()) {
            throw new RuntimeException("无法解码图像数据");
        }
        
        Mat grayImage = new Mat();
        Mat binaryImage = new Mat();
        
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(grayImage, binaryImage, threshold, 255, Imgproc.THRESH_BINARY);
        
        MatOfByte matOfByte = new MatOfByte();
        boolean success = Imgcodecs.imencode(".jpg", binaryImage, matOfByte);
        if (!success) {
            throw new RuntimeException("无法编码处理后的图像");
        }
        
        byte[] encodedBytes = matOfByte.toArray();
        return saveProcessedImageToMinio(encodedBytes, "binary");
    }
}