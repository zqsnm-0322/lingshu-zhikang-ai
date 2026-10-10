package com.lingshu.zhikang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingshu.zhikang.entity.po.ChatMemoryRecord;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatMemoryMapper extends BaseMapper<ChatMemoryRecord> {
    
    @Select("SELECT * FROM chat_memory WHERE conversation_id = #{conversationId} ORDER BY created_time")
    List<ChatMemoryRecord> findAllByConversationId(String conversationId);
    
    @Delete("DELETE FROM chat_memory WHERE conversation_id = #{conversationId}")
    int deleteByConversationId(String conversationId);
} 