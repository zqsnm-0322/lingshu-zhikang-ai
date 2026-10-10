package com.lingshu.zhikang.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lingshu.zhikang.entity.po.ChatHistoryRecord;
import com.lingshu.zhikang.mapper.ChatCounterMapper;
import com.lingshu.zhikang.mapper.ChatHistoryMapper;
import com.lingshu.zhikang.mapper.ChatMemoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * MySQL实现的ChatHistoryRepository
 * 通过MySQL存储聊天历史记录
 */
@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class MysqlChatHistoryRepository implements ChatHistoryRepository {
    
    private final ChatHistoryMapper chatHistoryMapper;
    private final ChatMemoryMapper chatMemoryMapper;
    private final ChatCounterMapper chatCounterMapper;
    
    @Override
    public void save(String type, String chatId) {
        try {
            // 检查是否已存在相同记录
            LambdaQueryWrapper<ChatHistoryRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ChatHistoryRecord::getType, type)
                    .eq(ChatHistoryRecord::getConversationId, chatId);
            
            // 如果不存在则保存
            if (chatHistoryMapper.selectCount(queryWrapper) == 0) {
                ChatHistoryRecord record = ChatHistoryRecord.builder()
                        .type(type)
                        .conversationId(chatId)
                        .build();
                chatHistoryMapper.insert(record);
            }
        } catch (Exception e) {
            log.error("保存聊天历史记录失败: {}", e.getMessage(), e);
            throw new RuntimeException("保存聊天历史记录失败", e);
        }
    }
    
    @Override
    public List<String> getChatIds(String type) {
        try {
            return chatHistoryMapper.findConversationIdsByType(type);
        } catch (Exception e) {
            log.error("获取聊天历史ID列表失败: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    @Transactional
    public void delete(String type, String chatId) {
        try {
            // 删除聊天历史记录
            chatHistoryMapper.deleteByTypeAndConversationId(type, chatId);
        } catch (Exception e) {
            log.error("删除聊天历史记录失败: {}", e.getMessage(), e);
            throw new RuntimeException("删除聊天历史记录失败", e);
        }
    }
    
    @Override
    @Transactional
    public String generateChatId(String type) {
        try {
            // 获取所有已用ID
            List<String> chatIds = getChatIds(type);
            // 收集所有已用的正整数ID
            java.util.Set<Integer> usedIds = new java.util.HashSet<>();
            for (String chatId : chatIds) {
                try {
                    int id = Integer.parseInt(chatId);
                    if (id > 0) {
                        usedIds.add(id);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
            // 从1开始找最小未用ID
            int newId = 1;
            while (usedIds.contains(newId)) {
                newId++;
            }
            return String.valueOf(newId);
        } catch (Exception e) {
            log.error("生成对话ID失败: {}", e.getMessage(), e);
            // 如果失败，返回一个基于时间戳的ID作为备选
            return "t" + System.currentTimeMillis();
        }
    }
    
    /**
     * 获取现有对话ID的最大数值
     * @param type 业务类型
     * @return 最大ID值，如果没有记录则返回0
     */
    private int getMaxChatIdAsInt(String type) {
        List<String> chatIds = getChatIds(type);
        int maxId = 0;
        
        for (String chatId : chatIds) {
            try {
                // 尝试将ID转换为数字
                int id = Integer.parseInt(chatId);
                if (id > maxId) {
                    maxId = id;
                }
            } catch (NumberFormatException ignored) {
                // 忽略非数字格式的ID
            }
        }
        
        return maxId;
    }
} 