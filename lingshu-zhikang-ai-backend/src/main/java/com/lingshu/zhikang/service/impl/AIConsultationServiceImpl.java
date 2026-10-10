package com.lingshu.zhikang.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingshu.zhikang.service.IAIConsultationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI智能问诊服务实现类
 * 
 * @author ZQ
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIConsultationServiceImpl implements IAIConsultationService {
    
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 存储问诊会话数据（实际项目中应使用Redis或数据库）
    private final Map<String, ConsultationSession> sessions = new ConcurrentHashMap<>();
    
    // 紧急症状关键词
    private final Set<String> emergencyKeywords = Set.of(
        "胸痛", "呼吸困难", "意识模糊", "昏迷", "大出血", "剧烈腹痛", 
        "高烧不退", "抽搐", "中毒", "外伤", "骨折", "窒息", "休克"
    );
    
    // 常见症状到科室的映射
    private final Map<String, String> symptomToDepartment = Map.of(
        "头痛", "神经内科",
        "胸痛", "心内科",
        "腹痛", "消化内科",
        "咳嗽", "呼吸内科",
        "皮疹", "皮肤科",
        "关节痛", "骨科",
        "眼部", "眼科",
        "耳鸣", "耳鼻喉科"
    );
    
    @Override
    public Map<String, Object> startConsultation(Long userId, String initialSymptoms) {
        log.info("开始AI问诊: userId={}, symptoms={}", userId, initialSymptoms);
        
        // 生成会话ID
        String sessionId = generateSessionId();
        
        // 紧急症状检测
        Map<String, Object> emergencyResult = emergencyCheck(initialSymptoms);
        if ((Boolean) emergencyResult.get("isEmergency")) {
            return emergencyResult;
        }
        
        // 创建问诊会话
        ConsultationSession session = new ConsultationSession();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setInitialSymptoms(initialSymptoms);
        session.setStartTime(LocalDateTime.now());
        session.setCurrentQuestionIndex(0);
        
        // 生成问诊问题序列
        List<Question> questions = generateQuestionSequence(initialSymptoms);
        session.setQuestions(questions);
        
        sessions.put(sessionId, session);
        
        // 返回第一个问题
        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("totalQuestions", questions.size());
        result.put("currentQuestion", questions.get(0));
        result.put("progress", 1);
        
        return result;
    }
    
    @Override
    public Map<String, Object> answerQuestion(String sessionId, String questionId, String answer) {
        log.info("回答问诊问题: sessionId={}, questionId={}, answer={}", sessionId, questionId, answer);
        
        ConsultationSession session = sessions.get(sessionId);
        if (session == null) {
            throw new RuntimeException("问诊会话不存在");
        }
        
        // 记录答案
        session.getAnswers().put(questionId, answer);
        session.setCurrentQuestionIndex(session.getCurrentQuestionIndex() + 1);
        
        Map<String, Object> result = new HashMap<>();
        
        // 检查是否还有更多问题
        if (session.getCurrentQuestionIndex() < session.getQuestions().size()) {
            // 返回下一个问题
            Question nextQuestion = session.getQuestions().get(session.getCurrentQuestionIndex());
            result.put("hasMore", true);
            result.put("currentQuestion", nextQuestion);
            result.put("progress", session.getCurrentQuestionIndex() + 1);
            result.put("totalQuestions", session.getQuestions().size());
        } else {
            // 所有问题已完成，生成诊断
            result.put("hasMore", false);
            result.put("message", "问诊完成，正在生成诊断报告...");
            
            // 异步生成诊断报告
            Map<String, Object> diagnosis = generateDiagnosisReport(sessionId);
            result.put("diagnosis", diagnosis);
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> generateDiagnosisReport(String sessionId) {
        log.info("生成诊断报告: sessionId={}", sessionId);
        
        ConsultationSession session = sessions.get(sessionId);
        if (session == null) {
            throw new RuntimeException("问诊会话不存在");
        }
        
        try {
            // 构建AI提示词
            String prompt = buildDiagnosisPrompt(session);
            
            // 调用AI生成诊断
            String aiResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
            
            // 解析AI响应
            Map<String, Object> diagnosis = parseAIDiagnosis(aiResponse);
            
            // 添加专科推荐
            Map<String, Object> deptRecommendation = getDepartmentRecommendation(
                session.getInitialSymptoms(), 
                (String) diagnosis.get("primaryDiagnosis")
            );
            diagnosis.put("departmentRecommendation", deptRecommendation);
            
            // 保存诊断结果
            session.setDiagnosis(diagnosis);
            session.setEndTime(LocalDateTime.now());
            
            return diagnosis;
            
        } catch (Exception e) {
            log.error("生成诊断报告失败", e);
            throw new RuntimeException("生成诊断报告失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Map<String, Object>> getConsultationHistory(Long userId) {
        log.info("获取问诊历史: userId={}", userId);
        
        List<Map<String, Object>> history = new ArrayList<>();
        
        for (ConsultationSession session : sessions.values()) {
            if (session.getUserId().equals(userId) && session.getDiagnosis() != null) {
                Map<String, Object> record = new HashMap<>();
                record.put("sessionId", session.getSessionId());
                record.put("symptoms", session.getInitialSymptoms());
                record.put("startTime", session.getStartTime());
                record.put("endTime", session.getEndTime());
                record.put("primaryDiagnosis", session.getDiagnosis().get("primaryDiagnosis"));
                record.put("urgencyLevel", session.getDiagnosis().get("urgencyLevel"));
                
                history.add(record);
            }
        }
        
        // 按时间倒序排序
        history.sort((a, b) -> ((LocalDateTime) b.get("startTime")).compareTo((LocalDateTime) a.get("startTime")));
        
        return history;
    }
    
    @Override
    public Map<String, Object> getConsultationDetail(String sessionId) {
        log.info("获取问诊详情: sessionId={}", sessionId);
        
        ConsultationSession session = sessions.get(sessionId);
        if (session == null) {
            throw new RuntimeException("问诊会话不存在");
        }
        
        Map<String, Object> detail = new HashMap<>();
        detail.put("sessionId", session.getSessionId());
        detail.put("initialSymptoms", session.getInitialSymptoms());
        detail.put("questions", session.getQuestions());
        detail.put("answers", session.getAnswers());
        detail.put("diagnosis", session.getDiagnosis());
        detail.put("startTime", session.getStartTime());
        detail.put("endTime", session.getEndTime());
        
        return detail;
    }
    
    @Override
    public Map<String, Object> emergencyCheck(String symptoms) {
        log.info("紧急症状检测: symptoms={}", symptoms);
        
        Map<String, Object> result = new HashMap<>();
        
        boolean isEmergency = emergencyKeywords.stream()
            .anyMatch(keyword -> symptoms.contains(keyword));
        
        result.put("isEmergency", isEmergency);
        
        if (isEmergency) {
            result.put("urgencyLevel", "EMERGENCY");
            result.put("message", "检测到紧急症状，建议立即就医或拨打120急救电话！");
            result.put("recommendations", Arrays.asList(
                "立即前往最近的医院急诊科",
                "如症状严重请拨打120急救电话",
                "在等待救援期间保持冷静，避免剧烈活动",
                "如有家属陪同，请告知详细症状"
            ));
        } else {
            result.put("urgencyLevel", "NORMAL");
            result.put("message", "未检测到紧急症状，可以继续问诊");
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> getDepartmentRecommendation(String symptoms, String diagnosis) {
        log.info("获取专科推荐: symptoms={}, diagnosis={}", symptoms, diagnosis);
        
        Map<String, Object> result = new HashMap<>();
        
        // 根据症状和诊断推荐科室
        String recommendedDept = "内科"; // 默认科室
        
        for (Map.Entry<String, String> entry : symptomToDepartment.entrySet()) {
            if (symptoms.contains(entry.getKey()) || 
                (diagnosis != null && diagnosis.contains(entry.getKey()))) {
                recommendedDept = entry.getValue();
                break;
            }
        }
        
        result.put("primaryDepartment", recommendedDept);
        result.put("alternativeDepartments", getAlternativeDepartments(recommendedDept));
        result.put("reason", "根据您的症状和初步诊断推荐");
        
        return result;
    }
    
    /**
     * 生成问诊问题序列
     */
    private List<Question> generateQuestionSequence(String initialSymptoms) {
        List<Question> questions = new ArrayList<>();
        
        // 基础问题
        questions.add(new Question("q1", "请详细描述您的主要症状？", "text", null));
        questions.add(new Question("q2", "症状持续多长时间了？", "single", 
            Arrays.asList("1天以内", "1-3天", "3-7天", "1-2周", "2周以上")));
        questions.add(new Question("q3", "症状的严重程度如何？", "single",
            Arrays.asList("轻微", "中等", "严重", "非常严重")));
        questions.add(new Question("q4", "是否有发热？", "single",
            Arrays.asList("无发热", "低热(37.3-38℃)", "中等发热(38.1-39℃)", "高热(39℃以上)")));
        questions.add(new Question("q5", "是否有其他伴随症状？", "multiple",
            Arrays.asList("头痛", "恶心呕吐", "腹泻", "咳嗽", "乏力", "食欲不振", "无其他症状")));
        
        // 根据初始症状添加针对性问题
        if (initialSymptoms.contains("头痛")) {
            questions.add(new Question("q6", "头痛的性质是？", "single",
                Arrays.asList("胀痛", "刺痛", "跳痛", "压迫性疼痛")));
        }
        
        if (initialSymptoms.contains("腹痛")) {
            questions.add(new Question("q7", "腹痛的位置在？", "single",
                Arrays.asList("上腹部", "下腹部", "左腹部", "右腹部", "脐周")));
        }
        
        if (initialSymptoms.contains("咳嗽")) {
            questions.add(new Question("q8", "咳嗽是否有痰？", "single",
                Arrays.asList("干咳无痰", "少量白痰", "大量白痰", "黄痰", "血痰")));
        }
        
        // 既往史和用药史
        questions.add(new Question("q9", "是否有慢性疾病史？", "multiple",
            Arrays.asList("高血压", "糖尿病", "心脏病", "肝病", "肾病", "无慢性病")));
        questions.add(new Question("q10", "近期是否服用过药物？", "text", null));
        
        return questions;
    }
    
    /**
     * 构建诊断提示词
     */
    private String buildDiagnosisPrompt(ConsultationSession session) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("作为一名专业的AI医生助手，请根据以下问诊信息进行分析诊断：\n\n");
        prompt.append("患者主诉：").append(session.getInitialSymptoms()).append("\n\n");
        prompt.append("详细问诊记录：\n");
        
        for (Question question : session.getQuestions()) {
            String answer = session.getAnswers().get(question.getId());
            if (answer != null) {
                prompt.append("问：").append(question.getQuestion()).append("\n");
                prompt.append("答：").append(answer).append("\n\n");
            }
        }
        
        prompt.append("请按照以下JSON格式返回诊断结果：\n");
        prompt.append("{\n");
        prompt.append("  \"primaryDiagnosis\": \"主要诊断\",\n");
        prompt.append("  \"differentialDiagnosis\": [\"鉴别诊断1\", \"鉴别诊断2\"],\n");
        prompt.append("  \"urgencyLevel\": \"LOW/MEDIUM/HIGH/EMERGENCY\",\n");
        prompt.append("  \"confidence\": \"诊断置信度(0-100)\",\n");
        prompt.append("  \"symptoms\": [\"症状1\", \"症状2\"],\n");
        prompt.append("  \"recommendations\": [\"建议1\", \"建议2\"],\n");
        prompt.append("  \"treatmentSuggestions\": [\"治疗建议1\", \"治疗建议2\"],\n");
        prompt.append("  \"followUpAdvice\": \"随访建议\",\n");
        prompt.append("  \"redFlags\": [\"需要注意的危险信号\"],\n");
        prompt.append("  \"lifestyle\": [\"生活方式建议\"],\n");
        prompt.append("  \"explanation\": \"诊断解释和病情分析\"\n");
        prompt.append("}\n\n");
        prompt.append("请确保诊断准确、建议实用，并考虑患者的安全。");
        
        return prompt.toString();
    }
    
    /**
     * 解析AI诊断响应
     */
    private Map<String, Object> parseAIDiagnosis(String aiResponse) {
        try {
            // 尝试解析JSON
            return objectMapper.readValue(aiResponse, Map.class);
        } catch (JsonProcessingException e) {
            log.warn("AI响应不是标准JSON格式，使用文本解析: {}", aiResponse);
            
            // 如果不是JSON格式，创建默认结构
            Map<String, Object> diagnosis = new HashMap<>();
            diagnosis.put("primaryDiagnosis", "需要进一步检查");
            diagnosis.put("differentialDiagnosis", Arrays.asList("多种可能性"));
            diagnosis.put("urgencyLevel", "MEDIUM");
            diagnosis.put("confidence", 70);
            diagnosis.put("explanation", aiResponse);
            diagnosis.put("recommendations", Arrays.asList("建议到医院进一步检查", "注意休息，观察症状变化"));
            
            return diagnosis;
        }
    }
    
    /**
     * 获取备选科室
     */
    private List<String> getAlternativeDepartments(String primaryDept) {
        Map<String, List<String>> alternatives = Map.of(
            "内科", Arrays.asList("全科医学科", "急诊科"),
            "神经内科", Arrays.asList("内科", "急诊科"),
            "心内科", Arrays.asList("内科", "急诊科"),
            "消化内科", Arrays.asList("内科", "普外科"),
            "呼吸内科", Arrays.asList("内科", "急诊科"),
            "皮肤科", Arrays.asList("内科", "过敏反应科"),
            "骨科", Arrays.asList("急诊科", "康复科"),
            "眼科", Arrays.asList("神经内科", "内科"),
            "耳鼻喉科", Arrays.asList("内科", "神经内科")
        );
        
        return alternatives.getOrDefault(primaryDept, Arrays.asList("内科", "全科医学科"));
    }
    
    /**
     * 生成会话ID
     */
    private String generateSessionId() {
        return "consultation_" + System.currentTimeMillis() + "_" + 
               UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * 问诊会话数据结构
     */
    private static class ConsultationSession {
        private String sessionId;
        private Long userId;
        private String initialSymptoms;
        private List<Question> questions;
        private Map<String, String> answers = new HashMap<>();
        private int currentQuestionIndex;
        private Map<String, Object> diagnosis;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        
        // Getters and Setters
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getInitialSymptoms() { return initialSymptoms; }
        public void setInitialSymptoms(String initialSymptoms) { this.initialSymptoms = initialSymptoms; }
        
        public List<Question> getQuestions() { return questions; }
        public void setQuestions(List<Question> questions) { this.questions = questions; }
        
        public Map<String, String> getAnswers() { return answers; }
        public void setAnswers(Map<String, String> answers) { this.answers = answers; }
        
        public int getCurrentQuestionIndex() { return currentQuestionIndex; }
        public void setCurrentQuestionIndex(int currentQuestionIndex) { this.currentQuestionIndex = currentQuestionIndex; }
        
        public Map<String, Object> getDiagnosis() { return diagnosis; }
        public void setDiagnosis(Map<String, Object> diagnosis) { this.diagnosis = diagnosis; }
        
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    }
    
    /**
     * 问题数据结构
     */
    private static class Question {
        private String id;
        private String question;
        private String type; // text, single, multiple
        private List<String> options;
        
        public Question(String id, String question, String type, List<String> options) {
            this.id = id;
            this.question = question;
            this.type = type;
            this.options = options;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public List<String> getOptions() { return options; }
        public void setOptions(List<String> options) { this.options = options; }
    }
}