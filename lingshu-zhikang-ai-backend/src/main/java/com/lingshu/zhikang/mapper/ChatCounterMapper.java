package com.lingshu.zhikang.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ChatCounterMapper {
    
    /**
     * 获取并递增计数器
     * @param type 业务类型
     * @return 递增前的值
     */
    @Select("SELECT counter FROM chat_counter WHERE type = #{type} FOR UPDATE")
    Integer getCounter(@Param("type") String type);
    
    /**
     * 更新计数器值
     * @param type 业务类型
     * @param counter 新的计数器值
     * @return 影响的行数
     */
    @Update("UPDATE chat_counter SET counter = #{counter} WHERE type = #{type}")
    int updateCounter(@Param("type") String type, @Param("counter") Integer counter);
    
    /**
     * 插入计数器初始值
     * @param type 业务类型
     * @param initialValue 初始值
     * @return 影响的行数
     */
    @Insert("INSERT INTO chat_counter (type, counter) VALUES (#{type}, #{initialValue}) ON DUPLICATE KEY UPDATE counter = counter")
    int insertInitialCounter(@Param("type") String type, @Param("initialValue") Integer initialValue);
    
    /**
     * 检查计数器是否存在
     * @param type 业务类型
     * @return 存在返回1，不存在返回0
     */
    @Select("SELECT COUNT(*) FROM chat_counter WHERE type = #{type}")
    int checkCounterExists(@Param("type") String type);
} 