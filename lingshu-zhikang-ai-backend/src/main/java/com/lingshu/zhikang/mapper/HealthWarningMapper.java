package com.lingshu.zhikang.mapper;

import com.lingshu.zhikang.entity.po.HealthWarning;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 健康预警Mapper
 */
@Mapper
public interface HealthWarningMapper {

    /**
     * 创建预警记录
     */
    @Insert("INSERT INTO health_warning (user_id, rule_id, warning_type, warning_level, " +
            "indicator_type, indicator_value, warning_title, warning_content, suggestions, " +
            "risk_score, is_urgent, status) VALUES (#{userId}, #{ruleId}, #{warningType}, " +
            "#{warningLevel}, #{indicatorType}, #{indicatorValue}, #{warningTitle}, " +
            "#{warningContent}, #{suggestions}, #{riskScore}, #{isUrgent}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int createWarning(HealthWarning warning);

    /**
     * 获取用户的预警列表
     */
    @Select("SELECT * FROM health_warning WHERE user_id = #{userId} " +
            "ORDER BY created_time DESC LIMIT #{limit}")
    List<HealthWarning> getUserWarnings(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 获取用户未处理的预警
     */
    @Select("SELECT * FROM health_warning WHERE user_id = #{userId} AND status = 'pending' " +
            "ORDER BY is_urgent DESC, created_time DESC")
    List<HealthWarning> getPendingWarnings(@Param("userId") Long userId);

    /**
     * 更新预警状态
     */
    @Update("UPDATE health_warning SET status = #{status}, handled_time = #{handledTime}, " +
            "handled_note = #{handledNote} WHERE id = #{id}")
    int updateWarningStatus(HealthWarning warning);

    /**
     * 获取预警详情
     */
    @Select("SELECT * FROM health_warning WHERE id = #{id}")
    HealthWarning getWarningById(@Param("id") Long id);

    /**
     * 统计用户各级别预警数量
     */
    @Select("SELECT warning_level, COUNT(*) as count FROM health_warning " +
            "WHERE user_id = #{userId} AND created_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY warning_level")
    List<java.util.Map<String, Object>> countWarningsByLevel(@Param("userId") Long userId, @Param("days") int days);

    /**
     * 删除旧预警记录（超过指定天数）
     */
    @Delete("DELETE FROM health_warning WHERE created_time < DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    int deleteOldWarnings(@Param("days") int days);
}

