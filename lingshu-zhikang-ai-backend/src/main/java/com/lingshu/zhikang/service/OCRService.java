package com.lingshu.zhikang.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OCR文字识别服务
 * 支持百度OCR、腾讯云OCR等
 */
@Service
public class OCRService {

    @Value("${ocr.provider:baidu}")
    private String ocrProvider;

    @Value("${baidu.ocr.api-key:}")
    private String baiduApiKey;

    @Value("${baidu.ocr.secret-key:}")
    private String baiduSecretKey;

    private String accessToken;
    private long tokenExpireTime = 0;

    /**
     * 识别体检报告图像中的文字
     */
    public String recognizeText(byte[] imageBytes) throws Exception {
        if ("baidu".equals(ocrProvider)) {
            return recognizeWithBaidu(imageBytes);
        } else {
            // 可以扩展其他OCR服务
            throw new UnsupportedOperationException("不支持的OCR服务: " + ocrProvider);
        }
    }

    /**
     * 使用百度OCR识别
     */
    private String recognizeWithBaidu(byte[] imageBytes) throws Exception {
        // 1. 获取access_token
        if (accessToken == null || System.currentTimeMillis() > tokenExpireTime) {
            refreshBaiduAccessToken();
        }

        // 2. 调用OCR API
        String apiUrl = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic?access_token=" + accessToken;
        
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // 3. 构建请求参数
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String params = "image=" + java.net.URLEncoder.encode(base64Image, "UTF-8");

        // 4. 发送请求
        try (OutputStream os = conn.getOutputStream()) {
            os.write(params.getBytes(StandardCharsets.UTF_8));
        }

        // 5. 读取响应
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        // 6. 解析结果
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result = mapper.readValue(response.toString(), Map.class);

        if (result.containsKey("error_code")) {
            throw new RuntimeException("OCR识别失败: " + result.get("error_msg"));
        }

        // 7. 提取文字
        StringBuilder text = new StringBuilder();
        List<Map<String, Object>> wordsResult = (List<Map<String, Object>>) result.get("words_result");
        
        if (wordsResult != null) {
            for (Map<String, Object> item : wordsResult) {
                text.append(item.get("words")).append("\n");
            }
        }

        return text.toString();
    }

    /**
     * 刷新百度access_token
     */
    private void refreshBaiduAccessToken() throws Exception {
        String authUrl = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials"
                + "&client_id=" + baiduApiKey
                + "&client_secret=" + baiduSecretKey;

        URL url = new URL(authUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result = mapper.readValue(response.toString(), Map.class);

        if (result.containsKey("access_token")) {
            this.accessToken = (String) result.get("access_token");
            // token有效期30天，提前1天刷新
            this.tokenExpireTime = System.currentTimeMillis() + 29L * 24 * 60 * 60 * 1000;
        } else {
            throw new RuntimeException("获取百度access_token失败: " + result.get("error_description"));
        }
    }

    /**
     * 从OCR文本中提取健康数据
     */
    public Map<String, Object> extractHealthData(String ocrText) {
        Map<String, Object> healthData = new HashMap<>();

        if (ocrText == null || ocrText.isEmpty()) {
            return healthData;
        }

        // 血压识别 (例: 120/80, 血压120/80mmHg)
        Pattern bpPattern = Pattern.compile("(?:血压|BP|blood pressure)[：:]*\\s*(\\d{2,3})\\s*/\\s*(\\d{2,3})", 
                Pattern.CASE_INSENSITIVE);
        Matcher bpMatcher = bpPattern.matcher(ocrText);
        if (bpMatcher.find()) {
            healthData.put("systolic", Integer.parseInt(bpMatcher.group(1)));
            healthData.put("diastolic", Integer.parseInt(bpMatcher.group(2)));
        }

        // 血糖识别 (例: 血糖5.6, GLU 5.6 mmol/L)
        Pattern glucosePattern = Pattern.compile("(?:血糖|GLU|glucose)[：:]*\\s*(\\d+\\.?\\d*)", 
                Pattern.CASE_INSENSITIVE);
        Matcher glucoseMatcher = glucosePattern.matcher(ocrText);
        if (glucoseMatcher.find()) {
            healthData.put("bloodSugar", Double.parseDouble(glucoseMatcher.group(1)));
        }

        // 心率识别 (例: 心率72, HR 72 bpm)
        Pattern hrPattern = Pattern.compile("(?:心率|HR|heart rate)[：:]*\\s*(\\d{2,3})", 
                Pattern.CASE_INSENSITIVE);
        Matcher hrMatcher = hrPattern.matcher(ocrText);
        if (hrMatcher.find()) {
            healthData.put("heartRate", Integer.parseInt(hrMatcher.group(1)));
        }

        // 血红蛋白识别 (例: 血红蛋白130, HGB 130 g/L)
        Pattern hgbPattern = Pattern.compile("(?:血红蛋白|HGB|hemoglobin)[：:]*\\s*(\\d{2,3})", 
                Pattern.CASE_INSENSITIVE);
        Matcher hgbMatcher = hgbPattern.matcher(ocrText);
        if (hgbMatcher.find()) {
            healthData.put("hemoglobin", Integer.parseInt(hgbMatcher.group(1)));
        }

        // 体温识别 (例: 体温36.5, T 36.5℃)
        Pattern tempPattern = Pattern.compile("(?:体温|T|temperature)[：:]*\\s*(\\d{2}\\.\\d)", 
                Pattern.CASE_INSENSITIVE);
        Matcher tempMatcher = tempPattern.matcher(ocrText);
        if (tempMatcher.find()) {
            healthData.put("temperature", Double.parseDouble(tempMatcher.group(1)));
        }

        // 体重识别 (例: 体重65kg, Weight 65 kg)
        Pattern weightPattern = Pattern.compile("(?:体重|weight)[：:]*\\s*(\\d{2,3})", 
                Pattern.CASE_INSENSITIVE);
        Matcher weightMatcher = weightPattern.matcher(ocrText);
        if (weightMatcher.find()) {
            healthData.put("weight", Integer.parseInt(weightMatcher.group(1)));
        }

        // 身高识别 (例: 身高170cm, Height 170 cm)
        Pattern heightPattern = Pattern.compile("(?:身高|height)[：:]*\\s*(\\d{2,3})", 
                Pattern.CASE_INSENSITIVE);
        Matcher heightMatcher = heightPattern.matcher(ocrText);
        if (heightMatcher.find()) {
            healthData.put("height", Integer.parseInt(heightMatcher.group(1)));
        }

        // 计算BMI
        if (healthData.containsKey("weight") && healthData.containsKey("height")) {
            int weight = (int) healthData.get("weight");
            int height = (int) healthData.get("height");
            double bmi = weight / Math.pow(height / 100.0, 2);
            healthData.put("bmi", Math.round(bmi * 10) / 10.0);
        }

        // 总胆固醇识别 (例: TC 5.2 mmol/L)
        Pattern tcPattern = Pattern.compile("(?:总胆固醇|TC|cholesterol)[：:]*\\s*(\\d+\\.?\\d*)", 
                Pattern.CASE_INSENSITIVE);
        Matcher tcMatcher = tcPattern.matcher(ocrText);
        if (tcMatcher.find()) {
            healthData.put("totalCholesterol", Double.parseDouble(tcMatcher.group(1)));
        }

        // 甘油三酯识别 (例: TG 1.5 mmol/L)
        Pattern tgPattern = Pattern.compile("(?:甘油三酯|TG|triglyceride)[：:]*\\s*(\\d+\\.?\\d*)", 
                Pattern.CASE_INSENSITIVE);
        Matcher tgMatcher = tgPattern.matcher(ocrText);
        if (tgMatcher.find()) {
            healthData.put("triglyceride", Double.parseDouble(tgMatcher.group(1)));
        }

        return healthData;
    }

    /**
     * 模拟OCR识别（用于测试，无需真实API）
     */
    public String mockRecognizeText(byte[] imageBytes) {
        // 返回模拟的体检报告文本
        return """
            体检报告
            姓名: 张三
            性别: 男
            年龄: 35岁
            
            生命体征:
            血压: 135/85 mmHg
            心率: 78 次/分
            体温: 36.5 ℃
            身高: 175 cm
            体重: 72 kg
            
            血液检查:
            血糖: 6.2 mmol/L
            血红蛋白: 145 g/L
            总胆固醇: 5.8 mmol/L
            甘油三酯: 2.1 mmol/L
            
            诊断意见:
            血压偏高，血糖偏高，建议控制饮食，适量运动。
            """;
    }
}
