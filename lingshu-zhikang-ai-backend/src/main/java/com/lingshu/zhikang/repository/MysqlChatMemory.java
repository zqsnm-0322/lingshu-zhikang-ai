package com.lingshu.zhikang.repository;

import com.lingshu.zhikang.entity.po.ChatMemoryRecord;
import com.lingshu.zhikang.mapper.ChatMemoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MySQL实现的ChatMemory
 * 实现Spring AI的ChatMemory接口，通过MySQL存储会话记忆
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class MysqlChatMemory implements ChatMemory {
    
    private final ChatMemoryMapper chatMemoryMapper;
    
    @Override
    public void add(String conversationId, List<Message> messages) {
        if (messages == null || messages.isEmpty()) { return; }
        try {
            for (Message message : messages) {
                ChatMemoryRecord record = ChatMemoryRecord.fromMessage(conversationId, message);
                chatMemoryMapper.insert(record);
            }
        } catch (Exception e) {
            log.error("Failed to add messages to chat memory: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add messages to chat memory", e);
        }
    }
    
    @Override
    public List<Message> get(String conversationId, int maxResults) {
        try {
            List<ChatMemoryRecord> records = chatMemoryMapper.findAllByConversationId(conversationId);
            if (records == null || records.isEmpty()) {
                log.debug("No chat memory records found for conversation: {}", conversationId);
                return Collections.emptyList();
            }
            return records.stream()
                    .map(record -> {
                        try { return record.toMessage(); }
                        catch (Exception e) {
                            log.warn("Failed to convert chat memory record: {}, error: {}", record.getId(), e.getMessage());
                            return null;
                        }
                    })
                    .filter(message -> message != null)
                    .limit(maxResults)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get messages from chat memory: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get messages from chat memory", e);
        }
    }
    
    @Override
    @Transactional
    public void clear(String conversationId) {
        try { chatMemoryMapper.deleteByConversationId(conversationId); }
        catch (Exception e) {
            log.error("Failed to clear chat memory: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to clear chat memory", e);
        }
    }
    
    public Message getGeneration(String conversationId) {
        List<Message> messages = get(conversationId, 1);
        return messages.isEmpty() ? null : messages.get(messages.size() - 1);
    }
}