package com.lingshu.zhikang.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 图像AI观察服务，仅描述图像中可见内容，不以照片推断疾病或体质。
 */
@Service
public class ImageAIDiagnosisService {

    @Value("${ai.provider:qwen}")
    private String aiProvider;

    @Value("${dashscope.api-key:}")
    private String dashscopeApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 基于图像的AI健康诊断
     */
    public Map<String, Object> diagnoseFromImage(byte[] imageBytes) throws Exception {
        System.out.println("=== AI服务配置 ===");
        System.out.println("AI Provider: " + aiProvider);
        System.out.println("API Key: " + (dashscopeApiKey != null && !dashscopeApiKey.isEmpty() ? 
            dashscopeApiKey.substring(0, 10) + "..." : "未配置"));
        
        if ("qwen".equals(aiProvider)) {
            System.out.println("使用通义千问-VL模式");
            return diagnoseWithQwenVL(imageBytes);
        } else if ("mock".equals(aiProvider)) {
            System.out.println("使用模拟诊断模式");
            throw new IllegalStateException("图像分析服务处于演示配置，未连接可用的多模态模型");
        } else {
            throw new UnsupportedOperationException("不支持的AI服务: " + aiProvider);
        }
    }

    /**
     * 使用通义千问-VL进行图像诊断
     */
    private Map<String, Object> diagnoseWithQwenVL(byte[] imageBytes) throws Exception {
        System.out.println("========================================");
        System.out.println("🚀 正在调用通义千问-VL API");
        System.out.println("========================================");
        
        // 1. 图像转Base64
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        System.out.println("✓ 图像Base64编码完成，长度: " + base64Image.length());

        // 2. 构建请求
        String apiUrl = "https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation";
        System.out.println("✓ API地址: " + apiUrl);
        if (dashscopeApiKey == null || dashscopeApiKey.isBlank()) {
            throw new IllegalStateException("图像分析服务未配置 API Key");
        }

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + dashscopeApiKey);
        
        System.out.println("✓ HTTP连接已建立");

        // 3. 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "qwen-vl-max");

        Map<String, Object> input = new HashMap<>();
        List<Map<String, Object>> messages = new ArrayList<>();

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");

        List<Map<String, Object>> content = new ArrayList<>();

        // 文本提示
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("text", buildImageDiagnosisPrompt());
        content.add(textContent);

        // 图像内容
        Map<String, Object> imageContent = new HashMap<>();
        imageContent.put("image", "data:image/jpeg;base64," + base64Image);
        content.add(imageContent);

        message.put("content", content);
        messages.add(message);

        input.put("messages", messages);
        requestBody.put("input", input);

        // 参数设置
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("result_format", "message");
        requestBody.put("parameters", parameters);

        // 4. 发送请求
        String jsonRequest = objectMapper.writeValueAsString(requestBody);
        System.out.println("✓ 请求体已构建，大小: " + jsonRequest.length() + " bytes");
        System.out.println("⏳ 正在发送请求到通义千问服务器...");
        
        long startTime = System.currentTimeMillis();
        
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
        }
        
        System.out.println("✓ 请求已发送，等待响应...");

        // 5. 读取响应
        int responseCode = conn.getResponseCode();
        long endTime = System.currentTimeMillis();
        
        System.out.println("✓ 收到响应，状态码: " + responseCode);
        System.out.println("✓ 响应时间: " + (endTime - startTime) + " ms");
        
        StringBuilder response = new StringBuilder();
        
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    responseCode == 200 ? conn.getInputStream() : conn.getErrorStream(),
                    StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        if (responseCode != 200) {
            System.err.println("❌ API调用失败！");
            System.err.println("错误响应: " + response.toString());
            throw new RuntimeException("AI诊断失败: " + response.toString());
        }
        
        System.out.println("✓ 响应内容长度: " + response.length() + " bytes");

        // 6. 解析响应
        Map<String, Object> result = objectMapper.readValue(response.toString(), Map.class);
        
        // 提取AI返回的文本
        Map<String, Object> output = (Map<String, Object>) result.get("output");
        List<Map<String, Object>> choices = (List<Map<String, Object>>) output.get("choices");
        Map<String, Object> firstChoice = choices.get(0);
        Map<String, Object> messageResult = (Map<String, Object>) firstChoice.get("message");
        List<Map<String, Object>> contentResult = (List<Map<String, Object>>) messageResult.get("content");
        String aiResponse = (String) contentResult.get(0).get("text");

        System.out.println("✓ AI响应内容: " + aiResponse.substring(0, Math.min(200, aiResponse.length())) + "...");
        System.out.println("========================================");
        System.out.println("✅ 通义千问-VL API调用成功！");
        System.out.println("========================================");

        // 7. 解析AI返回的JSON诊断结果
        return parseAIResponse(aiResponse);
    }

    /**
     * 构建图像诊断提示词
     */
    private String buildImageDiagnosisPrompt() {
        return """
            你是图像质量与可见特征观察助手。只描述图像中可以直接观察到的内容，禁止诊断疾病、推断气虚/血虚等体质，禁止从外貌推断用户自述症状。
            
            请从以下维度分析：
            1. 图像是否清晰、曝光是否合适
            2. 是否检测到正面人像
            3. 是否存在遮挡或光线干扰
            4. 给出改善拍摄质量的建议
            
            请严格以JSON格式返回，不要有任何其他文字：
            {
                "mainDiagnosis": "图像质量观察结论（非医疗诊断）",
                "qiDeficiencySymptoms": [],
                "bloodDeficiencySymptoms": [],
                "combinedSymptoms": [],
                "suggestions": ["拍摄质量建议1", "拍摄质量建议2"],
                "confidence": 85
            }
            
            注意事项：
            1. 明确说明单张照片不能完成医学体检或诊断
            2. 不得输出疾病、体质、症状或治疗建议
            3. 置信度仅表示模型对图像质量观察的把握，范围0-100
            5. 必须返回纯JSON格式，不要有markdown标记
            6. 所有文本内容都不要包含星号(*)标记
            """;
    }

    /**
     * 解析AI返回的诊断结果
     */
    private Map<String, Object> parseAIResponse(String response) {
        try {
            // 提取JSON部分（去除可能的markdown标记）
            String jsonStr = response.trim();
            
            // 移除markdown代码块标记
            if (jsonStr.startsWith("```json")) {
                jsonStr = jsonStr.substring(7);
            } else if (jsonStr.startsWith("```")) {
                jsonStr = jsonStr.substring(3);
            }
            
            if (jsonStr.endsWith("```")) {
                jsonStr = jsonStr.substring(0, jsonStr.length() - 3);
            }
            
            jsonStr = jsonStr.trim();
            
            // 查找JSON对象的开始和结束
            int start = jsonStr.indexOf("{");
            int end = jsonStr.lastIndexOf("}") + 1;
            
            if (start >= 0 && end > start) {
                jsonStr = jsonStr.substring(start, end);
            }

            // 解析JSON
            Map<String, Object> diagnosis = objectMapper.readValue(jsonStr, Map.class);
            
            // 确保必要字段存在
            diagnosis.putIfAbsent("mainDiagnosis", "图像观察完成（非医疗诊断）");
            diagnosis.putIfAbsent("confidence", 85);
            diagnosis.putIfAbsent("qiDeficiencySymptoms", new ArrayList<>());
            diagnosis.putIfAbsent("bloodDeficiencySymptoms", new ArrayList<>());
            diagnosis.putIfAbsent("combinedSymptoms", new ArrayList<>());
            diagnosis.putIfAbsent("suggestions", Arrays.asList("保持良好作息", "均衡饮食", "适量运动"));
            
            return diagnosis;
            
        } catch (Exception e) {
            System.err.println("解析AI响应失败: " + e.getMessage());
            System.err.println("原始响应: " + response);
            
            throw new IllegalStateException("AI 返回格式无法解析，未生成分析结果", e);
        }
    }

    /**
     * 创建默认诊断结果（解析失败时使用）
     */
    private Map<String, Object> createDefaultDiagnosis() {
        Map<String, Object> diagnosis = new HashMap<>();
        diagnosis.put("mainDiagnosis", "AI分析完成，建议保持健康生活方式");
        diagnosis.put("faceColor", "面色正常");
        diagnosis.put("mentalState", "精神状态良好");
        diagnosis.put("eyeCondition", "眼神正常");
        diagnosis.put("skinCondition", "皮肤状况正常");
        diagnosis.put("constitution", "正常");
        diagnosis.put("qiDeficiencySymptoms", new ArrayList<>());
        diagnosis.put("bloodDeficiencySymptoms", new ArrayList<>());
        diagnosis.put("combinedSymptoms", new ArrayList<>());
        diagnosis.put("suggestions", Arrays.asList(
            "保持规律作息，每天睡眠7-8小时",
            "均衡饮食，多吃蔬菜水果",
            "适量运动，每周至少3次有氧运动",
            "保持心情愉悦，减少压力"
        ));
        diagnosis.put("confidence", 80);
        return diagnosis;
    }

    /**
     * 模拟AI诊断（用于测试，无需真实API）
     */
    public Map<String, Object> mockDiagnosis() {
        Map<String, Object> diagnosis = new HashMap<>();
        
        // 随机生成诊断结果
        String[] constitutions = {"气虚", "血虚", "气血两虚", "正常"};
        String constitution = constitutions[new Random().nextInt(constitutions.length)];
        
        diagnosis.put("mainDiagnosis", "面色略显疲惫，建议注意休息");
        diagnosis.put("faceColor", "面色略显苍白");
        diagnosis.put("mentalState", "精神状态一般");
        diagnosis.put("eyeCondition", "眼神略显疲惫");
        diagnosis.put("skinCondition", "皮肤略显干燥");
        diagnosis.put("constitution", constitution);
        
        List<String> qiSymptoms = new ArrayList<>();
        if (constitution.contains("气虚")) {
            qiSymptoms.addAll(Arrays.asList("面色苍白", "气短懒言", "容易疲劳"));
        }
        diagnosis.put("qiDeficiencySymptoms", qiSymptoms);
        
        List<String> bloodSymptoms = new ArrayList<>();
        if (constitution.contains("血虚")) {
            bloodSymptoms.addAll(Arrays.asList("面色萎黄", "唇色淡白", "头晕目眩"));
        }
        diagnosis.put("bloodDeficiencySymptoms", bloodSymptoms);
        
        diagnosis.put("combinedSymptoms", Arrays.asList("神疲乏力", "心悸失眠"));
        
        diagnosis.put("suggestions", Arrays.asList(
            "保证充足睡眠，每天7-8小时",
            "适当补充营养，多吃红枣、桂圆等补气血食物",
            "避免过度劳累，注意劳逸结合",
            "可以适当进行太极、瑜伽等舒缓运动",
            "保持心情愉悦，避免情绪波动"
        ));
        
        diagnosis.put("confidence", 88);
        
        return diagnosis;
    }

    /**
     * 格式化诊断结果为前端需要的格式
     */
    public Map<String, Object> formatDiagnosisResult(Map<String, Object> diagnosis) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("diagnosis", diagnosis);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
}
