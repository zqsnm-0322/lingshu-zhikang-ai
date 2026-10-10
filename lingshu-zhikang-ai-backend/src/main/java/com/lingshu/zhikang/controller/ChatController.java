package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.Media;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class ChatController {

    private final ChatClient chatClient;

    private final ChatHistoryRepository chatHistoryRepository;

    @RequestMapping(value = "/chat", produces = "text/html;charset=utf-8")
    public Flux<String> chat(
            @RequestParam("prompt") String prompt,
            @RequestParam("chatId") String chatId,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        // 1.保存会话id
        chatHistoryRepository.save("chat", chatId);
        // 2.请求模型
        if (files == null || files.isEmpty()) {
            // 没有附件，纯文本聊天
            return textChat(prompt, chatId);
        } else {
            // 有附件，多模态聊天
            return multiModalChat(prompt, chatId, files);
        }
    }
    
    /**
     * 生成新的会话ID
     * @param type 会话类型，如：chat、service、pdf
     * @return 新会话ID
     */
    @GetMapping("/generate-id/{type}")
    public ResponseEntity<String> generateChatId(@PathVariable String type) {
        String chatId = chatHistoryRepository.generateChatId(type);
        return ResponseEntity.ok(chatId);
    }

    /**
     * 新增：保存对话ID（不发消息，仅注册ID）
     */
    @GetMapping("/save-chat-id/{type}/{chatId}")
    public ResponseEntity<Void> saveChatId(@PathVariable String type, @PathVariable String chatId) {
        chatHistoryRepository.save(type, chatId);
        return ResponseEntity.ok().build();
    }

    private Flux<String> multiModalChat(String prompt, String chatId, List<MultipartFile> files) {
        // 1.解析多媒体
        List<Media> medias = files.stream()
                .map(file -> new Media(
                                MimeType.valueOf(Objects.requireNonNull(file.getContentType())),
                                file.getResource()
                        )
                )
                .toList();
        // 2.请求模型
        return chatClient.prompt()
                .user(p -> p.text(prompt).media(medias.toArray(Media[]::new)))
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .stream()
                .content()
                .onErrorResume(e -> {
                    // 忽略客户端断开连接的错误
                    if (e instanceof java.io.IOException && 
                        e.getMessage().contains("你的主机中的软件中止了一个已建立的连接")) {
                        return Flux.empty();
                    }
                    return Flux.error(e);
                });
    }

    private Flux<String> textChat(String prompt, String chatId) {
        return chatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .stream()
                .content()
                .onErrorResume(e -> {
                    // 忽略客户端断开连接的错误
                    if (e instanceof java.io.IOException && 
                        e.getMessage().contains("你的主机中的软件中止了一个已建立的连接")) {
                        return Flux.empty();
                    }
                    return Flux.error(e);
                });
    }
}
