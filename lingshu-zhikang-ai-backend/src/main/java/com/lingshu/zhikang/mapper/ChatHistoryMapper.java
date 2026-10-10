package com.lingshu.zhikang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingshu.zhikang.entity.po.ChatHistoryRecord;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatHistoryMapper extends BaseMapper<ChatHistoryRecord> {
    
    @Select("SELECT * FROM chat_history WHERE type = #{type}")
    List<ChatHistoryRecord> findAllByType(String type);
    
    @Delete("DELETE FROM chat_history WHERE type = #{type} AND conversation_id = #{conversationId}")
    int deleteByTypeAndConversationId(String type, String conversationId);
    
    @Select("SELECT conversation_id FROM chat_history WHERE type = #{type} ORDER BY id DESC")
    List<String> findConversationIdsByType(String type);

    @Select("SELECT COUNT(DISTINCT conversation_id) FROM chat_history WHERE type = 'chat'")
    int countTotalConversations();
}
