package com.lingshu.zhikang.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.lingshu.zhikang.entity.po.Msg;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryChatHistoryRepository implements ChatHistoryRepository {

    private Map<String, List<String>> chatHistory;

    private Map<String, Integer> chatIdCounters;

    private final ObjectMapper objectMapper;

    private final ChatMemory chatMemory;

    @Override
    public void save(String type, String chatId) {
        /*if (!chatHistory.containsKey(type)) {
            chatHistory.put(type, new ArrayList<>());
        }
        List<String> chatIds = chatHistory.get(type);*/
        List<String> chatIds = chatHistory.computeIfAbsent(type, k -> new ArrayList<>());
        if (chatIds.contains(chatId)) {
            return;
        }
        chatIds.add(chatId);
    }

    @Override
    public List<String> getChatIds(String type) {
        /*List<String> chatIds = chatHistory.get(type);
        return chatIds == null ? List.of() : chatIds;*/
        return chatHistory.getOrDefault(type, List.of());
    }

    @Override
    public void delete(String type, String chatId) {
        List<String> chatIds = chatHistory.get(type);
        if (chatIds != null) {
            chatIds.remove(chatId);
            // 保存到文件
            persistent();
        }
    }

    @Override
    public String generateChatId(String type) {
        // 从计数器中获取当前ID
        Integer currentId = chatIdCounters.getOrDefault(type, 0);
        
        // 查找现有历史中的最大ID
        int maxHistoryId = 0;
        List<String> chatIds = chatHistory.getOrDefault(type, List.of());
        for (String chatId : chatIds) {
            try {
                // 尝试将ID解析为数字
                int id = Integer.parseInt(chatId);
                if (id > maxHistoryId) {
                    maxHistoryId = id;
                }
            } catch (NumberFormatException ignored) {
                // 忽略非数字ID
            }
        }
        
        // 使用两者中的较大值加1作为新ID
        int nextId = Math.max(currentId, maxHistoryId) + 1;
        
        // 更新计数器并保存
        chatIdCounters.put(type, nextId);
        saveCountersToFile();
        
        return String.valueOf(nextId);
    }

    @PostConstruct
    private void init() {
        // 1.初始化会话历史记录和ID计数器
        this.chatHistory = new HashMap<>();
        this.chatIdCounters = new HashMap<>();
        // 2.读取本地会话历史和会话记忆
        FileSystemResource historyResource = new FileSystemResource("chat-history.json");
        FileSystemResource memoryResource = new FileSystemResource("chat-memory.json");
        FileSystemResource counterResource = new FileSystemResource("chat-counter.json");
        
        try {
            // 会话历史
            if (historyResource.exists()) {
                Map<String, List<String>> chatIds = this.objectMapper.readValue(historyResource.getInputStream(), new TypeReference<>() {
                });
                if (chatIds != null) {
                    this.chatHistory = chatIds;
                }
            }
            
            // 会话ID计数器
            if (counterResource.exists()) {
                Map<String, Integer> counters = this.objectMapper.readValue(counterResource.getInputStream(), new TypeReference<>() {
                });
                if (counters != null) {
                    this.chatIdCounters = counters;
                }
            }
            
            // 会话记忆
            if (memoryResource.exists()) {
                Map<String, List<Msg>> memory = this.objectMapper.readValue(memoryResource.getInputStream(), new TypeReference<>() {
                });
                if (memory != null) {
                    memory.forEach(this::convertMsgToMessage);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void convertMsgToMessage(String chatId, List<Msg> messages) {
        // 只有在使用 InMemoryChatMemory 时才恢复数据
        if (chatMemory instanceof InMemoryChatMemory) {
            this.chatMemory.add(chatId, messages.stream().map(Msg::toMessage).toList());
        } else {
            log.info("ChatMemory is not InMemoryChatMemory, skipping memory restoration");
        }
    }

    @PreDestroy
    private void persistent() {
        String history = toJsonString(this.chatHistory);
        String memory = getMemoryJsonString();
        String counters = toJsonString(this.chatIdCounters);
        
        FileSystemResource historyResource = new FileSystemResource("chat-history.json");
        FileSystemResource memoryResource = new FileSystemResource("chat-memory.json");
        FileSystemResource counterResource = new FileSystemResource("chat-counter.json");
        
        try (
                PrintWriter historyWriter = new PrintWriter(historyResource.getOutputStream(), true, StandardCharsets.UTF_8);
                PrintWriter memoryWriter = new PrintWriter(memoryResource.getOutputStream(), true, StandardCharsets.UTF_8);
                PrintWriter counterWriter = new PrintWriter(counterResource.getOutputStream(), true, StandardCharsets.UTF_8)
        ) {
            historyWriter.write(history);
            memoryWriter.write(memory);
            counterWriter.write(counters);
        } catch (IOException ex) {
            log.error("IOException occurred while saving files.", ex);
            throw new RuntimeException(ex);
        } catch (SecurityException ex) {
            log.error("SecurityException occurred while saving files.", ex);
            throw new RuntimeException(ex);
        } catch (NullPointerException ex) {
            log.error("NullPointerException occurred while saving files.", ex);
            throw new RuntimeException(ex);
        }
    }

    private String getMemoryJsonString() {
        // 检查 chatMemory 是否是 InMemoryChatMemory 的实例
        // 如果不是（例如使用了 MysqlChatMemory），则返回空的 JSON 对象
        if (!(chatMemory instanceof InMemoryChatMemory)) {
            log.info("ChatMemory is not InMemoryChatMemory, skipping memory persistence");
            return "{}";
        }
        
        Class<InMemoryChatMemory> clazz = InMemoryChatMemory.class;
        try {
            Field field = clazz.getDeclaredField("conversationHistory");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, List<Message>> memory = (Map<String, List<Message>>) field.get(chatMemory);
            Map<String, List<Msg>> memoryToSave = new HashMap<>();
            memory.forEach((chatId, messages) -> memoryToSave.put(chatId, messages.stream().map(Msg::new).toList()));
            return toJsonString(memoryToSave);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Failed to access conversationHistory field", e);
            return "{}";
        }
    }

    private String toJsonString(Object object) {
        ObjectWriter objectWriter = this.objectMapper.writerWithDefaultPrettyPrinter();
        try {
            return objectWriter.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing documentMap to JSON.", e);
        }
    }

    /**
     * 将计数器保存到文件
     */
    private void saveCountersToFile() {
        FileSystemResource counterResource = new FileSystemResource("chat-counter.json");
        try {
            this.objectMapper.writeValue(counterResource.getFile(), this.chatIdCounters);
        } catch (IOException ex) {
            throw new RuntimeException("保存计数器失败", ex);
        }
    }
}
