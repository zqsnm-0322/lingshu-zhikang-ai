package com.lingshu.zhikang.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "chat_memory", autoResultMap = true)
public class ChatMemoryRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String conversationId;
    
    private String messageType;
    
    private String messageText;
    
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;
    
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<AssistantMessage.ToolCall> toolCalls;
    
    private LocalDateTime createdTime;
    
    /**
     * 将记录转换为Message对象
     */
    public Message toMessage() {
        MessageType type = MessageType.valueOf(messageType);
        
        if (type == MessageType.SYSTEM) {
            return new SystemMessage(messageText);
        } else if (type == MessageType.USER) {
            return new UserMessage(messageText, List.of(), metadata != null ? metadata : Map.of());
        } else if (type == MessageType.ASSISTANT) {
            return new AssistantMessage(messageText, metadata != null ? metadata : Map.of(), toolCalls != null ? toolCalls : List.of(), List.of());
        } else {
            throw new IllegalArgumentException("Unsupported message type: " + messageType);
        }
    }
    
    /**
     * 从Message对象创建记录
     */
    public static ChatMemoryRecord fromMessage(String conversationId, Message message) {
        ChatMemoryRecord record = new ChatMemoryRecord();
        record.setConversationId(conversationId);
        record.setMessageType(message.getMessageType().name());
        record.setMessageText(message.getText());
        record.setMetadata(message.getMetadata() != null ? message.getMetadata() : Map.of());
        
        if (message instanceof AssistantMessage am) {
            record.setToolCalls(am.getToolCalls() != null ? am.getToolCalls() : List.of());
        }
        
        return record;
    }
} 