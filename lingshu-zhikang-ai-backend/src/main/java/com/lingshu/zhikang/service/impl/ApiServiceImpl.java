package com.lingshu.zhikang.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingshu.zhikang.entity.po.ApiCallLog;
import com.lingshu.zhikang.entity.po.EnterpriseCustomer;
import com.lingshu.zhikang.mapper.ApiCallLogMapper;
import com.lingshu.zhikang.mapper.EnterpriseCustomerMapper;
import com.lingshu.zhikang.service.IApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API服务实现类
 */
@Slf4j
@Service
public class ApiServiceImpl implements IApiService {
    
    @Autowired
    private EnterpriseCustomerMapper enterpriseCustomerMapper;
    
    @Autowired
    private ApiCallLogMapper apiCallLogMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder().build();
    
    @Override
    public String medicalQA(Long tenantId, String question) {
        log.info("医疗问答API调用 - 租户ID: {}, 问题: {}", tenantId, question);
        
        // 检查配额
        if (!checkApiQuota(tenantId)) {
            return "API调用配额已用完，请联系管理员";
        }
        
        // 记录API调用
        logApiCall(createApiLog(tenantId, "/api/v1/medical-qa", 
            "{\"question\":\"" + question + "\"}", 200, 150, new BigDecimal("0.10")));
        
        // 模拟AI回答（实际应该调用AI服务）
        String answer = generateMedicalAnswer(question);
        
        return answer;
    }
    
    @Override
    public Map<String, Object> symptomAnalysis(Long tenantId, String symptoms) {
        log.info("症状分析API调用 - 租户ID: {}, 症状: {}", tenantId, symptoms);
        
        if (!checkApiQuota(tenantId)) {
            Map<String, Object> result = new HashMap<>();
            result.put("error", "API调用配额已用完");
            return result;
        }
        
        logApiCall(createApiLog(tenantId, "/api/v1/symptom-analysis", 
            "{\"symptoms\":\"" + symptoms + "\"}", 200, 280, new BigDecimal("0.15")));
        
        Map<String, Object> result = new HashMap<>();
        result.put("symptoms", symptoms);
        result.put("possibleDiseases", analyzeSymptomsToDisease(symptoms));
        result.put("urgencyLevel", "MEDIUM");
        result.put("recommendations", new String[]{"建议就医检查", "注意休息", "多喝水"});
        
        return result;
    }
    
    @Override
    public Map<String, Object> medicineQuery(Long tenantId, String medicineName) {
        log.info("药物查询API调用 - 租户ID: {}, 药物: {}", tenantId, medicineName);
        
        if (!checkApiQuota(tenantId)) {
            Map<String, Object> result = new HashMap<>();
            result.put("error", "API调用配额已用完");
            return result;
        }

        long start = System.currentTimeMillis();
        Map<String, Object> result;
        try {
            result = queryMedicineFromInternet(medicineName);
        } catch (Exception e) {
            log.error("在线药物查询失败: {}", medicineName, e);
            result = new HashMap<>();
            result.put("name", medicineName);
            result.put("source", "internet");
            result.put("error", "在线药物查询失败");
        }

        long cost = System.currentTimeMillis() - start;
        logApiCall(createApiLog(tenantId, "/api/v1/medicine-query",
            "{\"medicineName\":\"" + medicineName + "\"}", 200, (int) cost, new BigDecimal("0.10")));

        return result;
    }
    
    @Override
    public String healthAdvice(Long tenantId, Map<String, Object> userProfile) {
        log.info("健康建议API调用 - 租户ID: {}, 用户画像: {}", tenantId, userProfile);
        
        if (!checkApiQuota(tenantId)) {
            return "API调用配额已用完";
        }
        
        logApiCall(createApiLog(tenantId, "/api/v1/health-advice", 
            userProfile.toString(), 200, 200, new BigDecimal("0.10")));
        
        Integer age = (Integer) userProfile.getOrDefault("age", 30);
        String gender = (String) userProfile.getOrDefault("gender", "male");
        
        return generateHealthAdvice(age, gender);
    }
    
    @Override
    public Long validateApiKey(String apiKey) {
        log.info("验证API密钥: {}", apiKey);
        
        EnterpriseCustomer customer = enterpriseCustomerMapper.selectByApiKey(apiKey);
        
        if (customer == null) {
            log.warn("无效的API密钥: {}", apiKey);
            return null;
        }
        
        if (!"ACTIVE".equals(customer.getStatus())) {
            log.warn("企业客户状态异常: {}", customer.getStatus());
            return null;
        }
        
        return customer.getId();
    }
    
    @Override
    public Boolean checkApiQuota(Long tenantId) {
        EnterpriseCustomer customer = enterpriseCustomerMapper.selectById(tenantId);
        
        if (customer == null) {
            return false;
        }
        
        // 检查配额
        boolean hasQuota = customer.getApiUsed() < customer.getApiQuota();
        
        if (hasQuota) {
            // 增加使用次数
            customer.setApiUsed(customer.getApiUsed() + 1);
            enterpriseCustomerMapper.updateById(customer);
        }
        
        return hasQuota;
    }
    
    @Override
    public void logApiCall(ApiCallLog log) {
        apiCallLogMapper.insert(log);
    }
    
    @Override
    public Map<String, Object> getApiStatistics(Long tenantId, String startDate, String endDate) {
        log.info("获取API统计 - 租户ID: {}, 时间范围: {} ~ {}", tenantId, startDate, endDate);
        
        List<ApiCallLog> logs = apiCallLogMapper.selectByTenantIdAndDateRange(tenantId, startDate, endDate);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalCalls", logs.size());
        statistics.put("successCalls", logs.stream().filter(l -> l.getResponseStatus() == 200).count());
        statistics.put("failedCalls", logs.stream().filter(l -> l.getResponseStatus() != 200).count());
        statistics.put("totalBilling", logs.stream()
            .map(ApiCallLog::getBillingAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        statistics.put("avgResponseTime", logs.stream()
            .mapToInt(ApiCallLog::getResponseTimeMs)
            .average()
            .orElse(0.0));
        
        return statistics;
    }

    /**
     * 在线药物查询（国内互联网实时数据）
     */
    private Map<String, Object> queryMedicineFromInternet(String medicineName) throws Exception {
        String encodedName = URLEncoder.encode(medicineName, StandardCharsets.UTF_8);

        // 国内药品接口（天行数据）
        String apiKey = System.getenv("TIANAPI_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = "";
        }

        String chinaDrugUrl = "https://api.tianapi.com/yaopin/index?key=" + apiKey + "&word=" + encodedName;
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(chinaDrugUrl))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = objectMapper.readTree(response.body());

        Map<String, Object> result = new HashMap<>();
        result.put("query", medicineName);
        result.put("source", "internet");
        result.put("sourceApi", "TianApi-Yaopin");

        int code = root.path("code").asInt(-1);
        JsonNode newsList = root.path("newslist");

        if (code == 200 && newsList.isArray() && !newsList.isEmpty()) {
            JsonNode first = newsList.get(0);
            result.put("name", first.path("name").asText(medicineName));
            result.put("indication", first.path("gongnengzhuzhi").asText(""));
            result.put("usage", first.path("yongfayongliang").asText(""));
            result.put("adverseReactions", first.path("buliangfanying").asText(""));
            result.put("contraindications", first.path("jinji").asText(""));
            result.put("specification", first.path("guige").asText(""));
            result.put("manufacturer", first.path("shengchanchangjia").asText(""));
            result.put("approvalNumber", first.path("pizhunwenhao").asText(""));
            result.put("raw", first);
            return result;
        }

        result.put("name", medicineName);
        result.put("message", root.path("msg").asText("未从国内在线接口查到该药品"));
        return result;
    }
    
    /**
     * 创建API调用日志
     */
    private ApiCallLog createApiLog(Long tenantId, String endpoint, String params, 
                                    Integer status, Integer responseTime, BigDecimal billing) {
        ApiCallLog log = new ApiCallLog();
        log.setTenantId(tenantId);
        log.setApiEndpoint(endpoint);
        log.setRequestParams(params);
        log.setResponseStatus(status);
        log.setResponseTimeMs(responseTime);
        log.setBillingAmount(billing);
        log.setCreatedAt(LocalDateTime.now());
        return log;
    }
    
    /**
     * 生成医疗问答答案（模拟）
     */
    private String generateMedicalAnswer(String question) {
        if (question.contains("感冒")) {
            return "感冒通常是由病毒引起的上呼吸道感染。建议多休息、多喝水，可以服用对症药物缓解症状。" +
                   "如果症状持续超过一周或出现高热、呼吸困难等情况，请及时就医。";
        } else if (question.contains("发烧")) {
            return "发烧是身体对感染的自然反应。体温在38.5°C以下可以物理降温，超过38.5°C建议服用退烧药。" +
                   "同时要注意补充水分，如果持续高热不退，请及时就医。";
        } else if (question.contains("头痛")) {
            return "头痛的原因很多，包括紧张性头痛、偏头痛、感冒等。建议先休息，避免过度用眼和精神紧张。" +
                   "如果头痛剧烈或伴有其他症状，建议就医检查。";
        } else {
            return "感谢您的提问。建议您详细描述症状，或直接咨询专业医生获取更准确的建议。" +
                   "如有紧急情况，请立即就医或拨打急救电话。";
        }
    }
    
    /**
     * 症状分析（模拟）
     */
    private Map<String, Double> analyzeSymptomsToDisease(String symptoms) {
        Map<String, Double> diseases = new HashMap<>();
        
        if (symptoms.contains("发烧") || symptoms.contains("咳嗽")) {
            diseases.put("感冒", 0.75);
            diseases.put("流感", 0.20);
            diseases.put("其他", 0.05);
        } else if (symptoms.contains("头痛") || symptoms.contains("头晕")) {
            diseases.put("紧张性头痛", 0.60);
            diseases.put("偏头痛", 0.25);
            diseases.put("其他", 0.15);
        } else {
            diseases.put("需要进一步检查", 1.0);
        }
        
        return diseases;
    }
    
    /**
     * 生成健康建议（模拟）
     */
    private String generateHealthAdvice(Integer age, String gender) {
        StringBuilder advice = new StringBuilder();
        
        advice.append("根据您的年龄和性别，我们为您提供以下健康建议：\n\n");
        
        if (age < 30) {
            advice.append("1. 保持规律作息，避免熬夜\n");
            advice.append("2. 坚持运动，每周至少3次有氧运动\n");
            advice.append("3. 注意饮食均衡，多吃蔬菜水果\n");
        } else if (age < 50) {
            advice.append("1. 定期体检，关注血压、血糖、血脂\n");
            advice.append("2. 适度运动，避免过度劳累\n");
            advice.append("3. 控制体重，预防慢性疾病\n");
            advice.append("4. 保持良好心态，减轻工作压力\n");
        } else {
            advice.append("1. 每年进行全面体检\n");
            advice.append("2. 适度运动，如散步、太极等\n");
            advice.append("3. 注意饮食清淡，控制盐分摄入\n");
            advice.append("4. 保持社交活动，预防老年痴呆\n");
        }
        
        if ("female".equals(gender)) {
            advice.append("5. 女性特别注意：定期妇科检查，补充钙质\n");
        }
        
        return advice.toString();
    }
}
