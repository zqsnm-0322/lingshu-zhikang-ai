package com.lingshu.zhikang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingshu.zhikang.entity.po.HealthDiary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 健康日记 Mapper 接口
 */
@Mapper
public interface HealthDiaryMapper extends BaseMapper<HealthDiary> {
    
    /**
     * 根据用户ID查询日记列表
     */
    @Select("SELECT * FROM health_diary WHERE user_id = #{userId} ORDER BY date DESC")
    List<HealthDiary> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和日期查询日记
     */
    @Select("SELECT * FROM health_diary WHERE user_id = #{userId} AND date = #{date}")
    HealthDiary selectByUserIdAndDate(@Param("userId") Long userId, @Param("date") String date);
    
    /**
     * 查询用户的统计数据
     */
    @Select("SELECT " +
            "COUNT(*) as totalDays, " +
            "AVG(mood) as avgMood, " +
            "AVG(sleep) as avgSleep " +
            "FROM health_diary " +
            "WHERE user_id = #{userId}")
    Map<String, Object> selectStatsByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和日期查询日记（别名方法）
     */
    @Select("SELECT * FROM health_diary WHERE user_id = #{userId} AND date = #{date}")
    HealthDiary getByUserIdAndDate(@Param("userId") Long userId, @Param("date") String date);
    
    /**
     * 查询用户最新的健康日记
     */
    @Select("SELECT * FROM health_diary WHERE user_id = #{userId} ORDER BY date DESC LIMIT 1")
    HealthDiary getLatestByUserId(@Param("userId") Long userId);
}

