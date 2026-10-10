package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.entity.vo.ResponseResult;
import com.lingshu.zhikang.repository.ChatHistoryRepository;
import com.lingshu.zhikang.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/pdf")
public class PdfController {

    private final FileRepository fileRepository;

    private final VectorStore vectorStore;

    private final ChatClient pdfChatClient;

    private final ChatHistoryRepository chatHistoryRepository;

    @RequestMapping(value = "/chat", produces = "text/html;charset=utf-8")
    public Flux<String> chat(String prompt, String chatId) {
        // 1.找到会话文件
        Resource file = fileRepository.getFile(chatId);
        if (!file.exists()) {
            // 文件不存在，不回答
            throw new RuntimeException("会话文件不存在！");
        }
        // 2.保存会话id
        chatHistoryRepository.save("pdf", chatId);
        
        // 3.从向量库检索相关内容，按chatId过滤
        try {
            // 使用向量相似性搜索获取相关文档片段，并过滤出当前会话的文档
            List<Document> allDocs = vectorStore.similaritySearch(prompt);
            
            // 过滤出属于当前chatId的文档
            List<Document> relevantDocs = allDocs.stream()
                    .filter(doc -> chatId.equals(doc.getMetadata().get("chat_id")))
                    .toList();
            
            // 检查是否检索到文档
            if (relevantDocs.isEmpty()) {
                log.warn("未能从向量库检索到chatId={}的病历内容，可能需要重新上传", chatId);
                return Flux.just("## ⚠️ 未找到病历数据\n\n" +
                        "抱歉，无法检索到您上传的病历内容。这可能是因为：\n\n" +
                        "1. **服务器重启**：向量数据库在内存中，重启后数据会丢失\n" +
                        "2. **上传失败**：PDF文件可能未成功处理\n" +
                        "3. **会话过期**：会话数据可能已被清理\n\n" +
                        "## 🔄 解决方法\n\n" +
                        "请**重新上传您的病历PDF文件**，然后再进行咨询。\n\n" +
                        "上传后，您可以：\n" +
                        "- 点击自动分析按钮\n" +
                        "- 输入具体问题进行咨询\n" +
                        "- 使用医生模拟器进行详细对话");
            }
            
            // 构建上下文信息
            StringBuilder contextBuilder = new StringBuilder();
            contextBuilder.append("根据上传的病历PDF文档内容，以下是相关信息：\n\n");
            
            for (Document doc : relevantDocs) {
                contextBuilder.append(doc.getText()).append("\n\n");
            }
            
            String context = contextBuilder.toString();
            
            // 构建专门针对病历分析的提示词
            String medicalPrompt = buildMedicalAnalysisPrompt(context, prompt);
            
            // 4.请求模型
            log.info("开始调用AI模型进行病历咨询，chatId: {}", chatId);
            return pdfChatClient.prompt()
                    .user(medicalPrompt)
                    .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                    .stream()
                    .content()
                    .doOnNext(chunk -> log.debug("收到AI响应块，长度: {}", chunk.length()))
                    .doOnComplete(() -> log.info("AI响应流完成，chatId: {}", chatId))
                    .doOnError(error -> log.error("AI模型调用出错，chatId: {}, 错误: ", chatId, error))
                    .onErrorResume(e -> {
                        // 忽略客户端断开连接的错误
                        if (e instanceof java.io.IOException && 
                            e.getMessage().contains("你的主机中的软件中止了一个已建立的连接")) {
                            log.debug("客户端断开连接（正常现象），chatId: {}", chatId);
                            return Flux.empty();
                        }
                        log.error("AI模型调用失败，chatId: {}", chatId, e);
                        return Flux.error(e);
                    });
                    
        } catch (Exception e) {
            log.error("检索向量库失败: ", e);
            return Flux.just("抱歉，无法从病历PDF文档中检索到相关信息。请确保文档已正确上传并处理。");
        }
    }

    /**
     * 构建病历分析专用提示词
     */
    private String buildMedicalAnalysisPrompt(String context, String userPrompt) {
        return String.format("""
            你是一位专业的医疗助手，请基于以下病历PDF文档内容进行分析：
            
            %s
            
            用户问题：%s
            
            请按照以下格式提供专业的医疗分析：
            
            ## 📋 病历解读
            - 详细解释病历中的医学术语和诊断结果
            - 分析检查指标的含义和异常情况
            - 说明病情的严重程度和可能的发展趋势
            
            ## 🍎 健康饮食建议
            - 根据病情提供具体的饮食指导
            - 推荐有益的食物和营养素
            - 列出需要避免或限制的食物
            - 提供日常饮食搭配建议
            
            ## ⚠️ 注意事项
            - 强调需要遵循的医嘱和注意事项
            - 提醒定期复查的重要性
            - 说明何时需要及时就医
            
            ## 💡 生活方式建议
            - 运动和休息的建议
            - 心理健康的关注
            - 其他有助于康复的生活习惯
            
            请注意：
            1. 所有建议仅供参考，不能替代专业医生的诊断和治疗
            2. 如有疑问或病情变化，请及时咨询医生
            3. 严格按照医生开具的处方用药，不要自行调整
            4. 保持积极乐观的心态，配合治疗
            
            如果病历中信息不完整或无法确定具体病情，请明确说明并建议咨询专业医生。
            """, context, userPrompt);
    }

    /**
     * 文件上传
     */
    @RequestMapping("/upload/{chatId}")
    public ResponseResult<Void> uploadPdf(@PathVariable String chatId, @RequestParam("file") MultipartFile file) {
        try {
            // 1. 校验文件是否为PDF格式
            if (!Objects.equals(file.getContentType(), "application/pdf")) {
                return ResponseResult.error("只能上传PDF文件！");
            }
            // 2.保存文件
            boolean success = fileRepository.save(chatId, file.getResource());
            if (!success) {
                return ResponseResult.error("保存文件失败！");
            }
            // 3.写入向量库，传入chatId用于标识
            this.writeToVectorStore(chatId, file.getResource());
            
            // 4.保存会话id，为自动分析做准备
            chatHistoryRepository.save("pdf", chatId);
            
            return ResponseResult.success(null);
        } catch (Exception e) {
            log.error("Failed to upload PDF.", e);
            return ResponseResult.error("上传文件失败！");
        }
    }

    /**
     * 自动分析上传的病历PDF
     */
    @RequestMapping(value = "/auto-analyze/{chatId}", produces = "text/html;charset=utf-8")
    public Flux<String> autoAnalyzeMedicalRecord(@PathVariable String chatId) {
        log.info("开始自动分析病历，chatId: {}", chatId);
        
        return Flux.defer(() -> {
            try {
                // 1.找到会话文件
                Resource file = fileRepository.getFile(chatId);
                if (!file.exists()) {
                    log.warn("病历文件不存在，chatId: {}", chatId);
                    return Flux.just("## ❌ 文件不存在\n\n病历文件不存在，无法进行分析。请重新上传PDF文件。");
                }
                
                // 2.等待一小段时间确保向量库写入完成
                Thread.sleep(3000);
                
                // 3.从向量库检索所有相关内容进行全面分析，并过滤当前chatId
                List<Document> allDocs = vectorStore.similaritySearch("病历 诊断 检查 化验 医嘱");
                
                // 过滤出属于当前chatId的文档
                List<Document> filteredDocs = allDocs.stream()
                        .filter(doc -> chatId.equals(doc.getMetadata().get("chat_id")))
                        .toList();
                
                log.info("从向量库检索到 {} 个文档片段，过滤后得到 {} 个当前会话的文档", 
                        allDocs.size(), filteredDocs.size());
                
                // 如果没有检索到当前chatId的文档，尝试使用更通用的搜索词
                if (filteredDocs.isEmpty()) {
                    log.warn("使用医疗关键词未检索到当前会话文档，尝试通用搜索");
                    allDocs = vectorStore.similaritySearch("患者 医院 治疗 药物");
                    filteredDocs = allDocs.stream()
                            .filter(doc -> chatId.equals(doc.getMetadata().get("chat_id")))
                            .toList();
                }
                
                // 如果还是没有，尝试检索所有文档
                if (filteredDocs.isEmpty()) {
                    log.warn("通用搜索也未检索到文档，尝试检索所有内容");
                    allDocs = vectorStore.similaritySearch("");
                    filteredDocs = allDocs.stream()
                            .filter(doc -> chatId.equals(doc.getMetadata().get("chat_id")))
                            .toList();
                }
                
                if (filteredDocs.isEmpty()) {
                    log.error("向量库中没有找到chatId={}的相关文档", chatId);
                    return Flux.just("## ⚠️ 未找到病历数据\n\n" +
                            "抱歉，无法检索到您上传的病历内容。\n\n" +
                            "**可能原因：**\n" +
                            "1. **服务器重启**：向量数据库在内存中，重启后数据会丢失\n" +
                            "2. **PDF解析失败**：文件可能是扫描件或图片格式\n" +
                            "3. **文件内容为空**：PDF可能损坏或加密\n\n" +
                            "**解决方法：**\n" +
                            "请**重新上传您的病历PDF文件**，确保：\n" +
                            "- 文件包含可选择的文本（非扫描件）\n" +
                            "- 文件未加密且内容完整\n" +
                            "- 上传后等待几秒钟再进行分析");
                }
                
                // 构建完整的病历内容
                StringBuilder contextBuilder = new StringBuilder();
                contextBuilder.append("以下是病历PDF文档的主要内容：\n\n");
                
                for (Document doc : filteredDocs) {
                    String text = doc.getText();
                    if (text != null && !text.trim().isEmpty()) {
                        contextBuilder.append(text.trim()).append("\n\n");
                    }
                }
                
                String context = contextBuilder.toString();
                log.info("构建的上下文长度: {} 字符", context.length());
                
                if (context.trim().length() < 50) {
                    log.warn("提取的文档内容过少，可能PDF解析有问题");
                    return Flux.just("## ⚠️ 内容过少\n\n从PDF中提取的内容过少，无法进行有效分析。\n\n**建议：**\n- 检查PDF文件是否包含可读的文本内容\n- 尝试重新上传文件\n- 或者手动描述病历内容进行咨询");
                }
                
                // 构建简化的自动分析提示词
                String autoAnalysisPrompt = buildSimplifiedAnalysisPrompt(context);
                log.info("开始调用AI模型进行分析");
                
                // 4.请求模型进行自动分析，添加超时和错误处理
                return pdfChatClient.prompt()
                        .user(autoAnalysisPrompt)
                        .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                        .stream()
                        .content()
                        .timeout(Duration.ofMinutes(2)) // 2分钟超时
                        .doOnNext(chunk -> log.debug("收到AI响应块: {}", chunk.length()))
                        .doOnError(error -> log.error("AI模型调用失败: ", error))
                        .onErrorResume(error -> {
                            // 客户端断开连接，静默处理
                            if (error instanceof java.io.IOException && 
                                error.getMessage().contains("你的主机中的软件中止了一个已建立的连接")) {
                                return Flux.empty();
                            }
                            // 超时错误
                            if (error instanceof java.util.concurrent.TimeoutException) {
                                return Flux.just("## ⏰ 分析超时\n\n分析请求超时，请稍后重试。如果问题持续存在，请手动提问获取帮助。");
                            }
                            // 其他错误
                            return Flux.just("## ❌ 分析失败\n\n抱歉，AI分析服务暂时不可用。\n\n**您可以：**\n- 稍后重试自动分析\n- 手动输入问题，如：\"请分析这份病历\"\n- 联系技术支持");
                        });
                        
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("等待向量库写入时被中断: ", e);
                return Flux.just("## ⚠️ 分析中断\n\n分析过程被中断，请重试。");
            } catch (Exception e) {
                log.error("自动分析病历失败，chatId: {}, 错误: ", chatId, e);
                return Flux.just("## ❌ 系统错误\n\n自动分析时出现系统错误：" + e.getMessage() + "\n\n请稍后重试或手动提问。");
            }
        });
    }

    /**
     * 构建简化的自动分析提示词（减少token消耗）
     */
    private String buildSimplifiedAnalysisPrompt(String context) {
        return String.format("""
            作为医疗助手，请分析以下病历内容并提供简洁的报告：
            
            病历内容：
            %s
            
            请提供：
            
            ## 📋 病历要点
            - 主要诊断和病情
            - 重要检查结果
            - 病情严重程度评估
            
            ## ⚠️ 注意事项
            - 重要医嘱和治疗建议
            - 复查提醒和时间安排
            - 需要警惕的症状变化
            
            ## 💡 生活指导
            - 日常注意事项
            - 活动和休息建议
            
            **声明：仅供参考，请遵医嘱**
            """, context.length() > 2000 ? context.substring(0, 2000) + "..." : context);
    }

    /**
     * 构建自动分析提示词
     */
    private String buildAutoAnalysisPrompt(String context) {
        return String.format("""
            你是一位专业的医疗助手。我刚刚上传了一份病历PDF文档，请对其进行全面的自动分析。
            
            病历内容：
            %s
            
            请提供以下完整的医疗分析报告：
            
            ## 🏥 病历概览
            - 患者基本信息（年龄、性别等，如有）
            - 主要诊断和病情描述
            - 就诊科室和医生建议
            
            ## 📋 详细病历解读
            - 详细解释病历中的医学术语和专业词汇
            - 分析各项检查指标的含义和异常情况
            - 解读化验结果和影像学检查
            - 说明病情的严重程度和可能的发展趋势
            
            ## 🍎 个性化健康饮食建议
            ### 推荐食物：
            - 根据具体病情推荐的有益食物
            - 富含所需营养素的食材
            - 具体的食物搭配建议
            
            ### 饮食禁忌：
            - 需要严格避免的食物
            - 需要限制摄入的食物类型
            - 饮食注意事项和时间安排
            
            ### 营养补充：
            - 可能需要的维生素和矿物质
            - 营养品选择建议
            - 日常饮水和作息建议
            
            ## ⚠️ 重要注意事项
            - 必须遵循的医嘱和用药指导
            - 定期复查的时间安排和重要性
            - 出现哪些症状时需要立即就医
            - 日常生活中的注意事项
            
            ## 💡 综合生活方式建议
            - 适合的运动类型和强度
            - 作息时间调整建议
            - 心理健康和情绪管理
            - 家庭护理要点
            
            ## 🔄 后续治疗建议
            - 预期的治疗周期和效果
            - 可能的并发症预防
            - 康复期间的注意事项
            
            ---
            
            **重要声明：**
            1. 以上分析仅供参考，不能替代专业医生的诊断和治疗
            2. 请严格按照医生处方用药，不要自行调整剂量
            3. 如有任何疑问或病情变化，请及时咨询主治医生
            4. 保持积极乐观的心态，配合医生治疗
            
            如果病历信息不够完整，我会根据现有信息提供建议，并提醒您咨询专业医生获取更详细的指导。
            """, context);
    }

    /**
     * 文件下载
     */
    @GetMapping("/file/{chatId}")
    public ResponseEntity<Resource> download(@PathVariable("chatId") String chatId) throws IOException {
        // 1.读取文件
        Resource resource = fileRepository.getFile(chatId);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        // 2.文件名编码，写入响应头
        String filename = resource.getFilename();
        if (filename == null) {
            filename = "document.pdf"; // 默认文件名
        }
        filename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        // 3.返回文件
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    private void writeToVectorStore(String chatId, Resource resource) {
        try {
            // 1.创建PDF的读取器
            PagePdfDocumentReader reader = new PagePdfDocumentReader(
                    resource, // 文件源
                    PdfDocumentReaderConfig.builder()
                            .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults())
                            .withPagesPerDocument(1) // 每1页PDF作为一个Document
                            .build()
            );
            // 2.读取PDF文档，拆分为Document
            List<Document> documents = reader.read();
            
            // 3.为每个文档添加元数据，特别是chatId用于后续检索过滤
            String fileName = resource.getFilename();
            if (fileName == null) {
                fileName = "document.pdf"; // 默认文件名
            }
            
            for (int i = 0; i < documents.size(); i++) {
                Document doc = documents.get(i);
                // 添加文件名、chatId和页码等元数据
                doc.getMetadata().put("chat_id", chatId);  // 关键：添加chatId
                doc.getMetadata().put("file_name", fileName);
                doc.getMetadata().put("page", i + 1);
                doc.getMetadata().put("source", "pdf");
            }
            
            // 4.写入向量库
            vectorStore.add(documents);
            log.info("成功将{}页PDF文档写入向量库，chatId: {}", documents.size(), chatId);
        } catch (Exception e) {
            log.error("写入向量库失败: ", e);
            throw new RuntimeException("PDF文档处理失败", e);
        }
    }
}