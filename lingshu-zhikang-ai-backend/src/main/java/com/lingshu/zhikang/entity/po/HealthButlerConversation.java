package com.lingshu.zhikang.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 健康管家对话记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthButlerConversation {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 对话会话ID
     */
    private String conversationId;

    /**
     * 消息类型：USER/ASSISTANT
     */
    private String messageType;

    /**
     * 消息内容
     */
    private String messageContent;

    /**
     * 情感识别：开心/难过/焦虑/平静
     */
    private String emotion;

    /**
     * 用户意图
     */
    private String intent;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}

