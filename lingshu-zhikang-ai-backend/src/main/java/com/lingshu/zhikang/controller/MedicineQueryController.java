package com.lingshu.zhikang.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.ai.chat.client.ChatClient;

import java.util.HashMap;
import java.util.Map;

/**
 * 面向普通用户的 AI 药品查询接口。
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medicine")
public class MedicineQueryController {

    @Qualifier("chatClient")
    private final ChatClient chatClient;

    private final ObjectMapper objectMapper;

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> query(@RequestBody Map<String, String> request) {
        String medicineName = request == null ? null : request.get("medicineName");
        if (!StringUtils.hasText(medicineName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请输入药品名称或成分");
        }

        String prompt = """
                你是一名严谨的药品信息助手。请根据药品名称或成分“%s”整理药品信息。
                只返回一个 JSON 对象，不要返回 Markdown、代码块或额外说明。
                JSON 必须包含以下字符串字段：
                {
                  "name": "药品名称",
                  "genericName": "通用名或成分",
                  "category": "药品分类",
                  "specification": "常见规格，未知时填写暂无",
                  "manufacturer": "生产企业，无法确认时填写需以包装为准",
                  "indications": "适应症",
                  "usage": "常见用法用量，并注明应以说明书或医嘱为准",
                  "contraindications": "禁忌",
                  "sideEffects": "常见不良反应",
                  "precautions": "注意事项",
                  "storage": "贮藏",
                  "validityPeriod": "有效期，未知时填写以包装为准",
                  "price": "参考价格，无法确认时填写暂无"
                }
                不要编造具体厂家、价格、规格或有效期；不确定的信息必须明确写“暂无”或“以包装为准”。
                """.formatted(medicineName.trim());

        try {
            String content = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            Map<String, Object> medicine = parseMedicine(content, medicineName.trim());

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "药品信息查询成功");
            result.put("data", medicine);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("AI 药品查询失败，药品：{}", medicineName, e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI 药品查询服务暂时不可用，请稍后重试");
        }
    }

    private Map<String, Object> parseMedicine(String content, String medicineName) throws Exception {
        if (!StringUtils.hasText(content)) {
            throw new IllegalStateException("AI 未返回药品信息");
        }

        String json = content.trim();
        if (json.startsWith("```") && json.endsWith("```")) {
            json = json.replaceFirst("^```(?:json)?\\s*", "")
                    .replaceFirst("\\s*```$", "")
                    .trim();
        }

        JsonNode node = objectMapper.readTree(json);
        if (!node.isObject()) {
            throw new IllegalStateException("AI 返回格式不是 JSON 对象");
        }

        Map<String, Object> result = objectMapper.convertValue(node, Map.class);
        result.putIfAbsent("name", medicineName);
        result.putIfAbsent("genericName", medicineName);
        result.putIfAbsent("price", "暂无");
        result.put("id", "ai-" + medicineName);
        result.put("source", "AI模型");
        return result;
    }
}
