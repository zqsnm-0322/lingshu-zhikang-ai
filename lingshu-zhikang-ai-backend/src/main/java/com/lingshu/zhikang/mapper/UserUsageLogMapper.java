package com.lingshu.zhikang.mapper;

import com.lingshu.zhikang.entity.po.UserUsageLog;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 用户使用记录Mapper
 */
@Mapper
public interface UserUsageLogMapper {

    /**
     * 插入使用记录
     */
    @Insert("INSERT INTO user_usage_log (user_id, feature_type, usage_date, usage_count, duration_seconds) " +
            "VALUES (#{userId}, #{featureType}, #{usageDate}, #{usageCount}, #{durationSeconds})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserUsageLog log);

    /**
     * 根据用户ID查询使用记录
     */
    @Select("SELECT * FROM user_usage_log WHERE user_id = #{userId} " +
            "ORDER BY usage_date DESC LIMIT #{limit}")
    List<UserUsageLog> selectByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 统计活跃用户数
     */
    @Select("SELECT COUNT(DISTINCT user_id) FROM user_usage_log " +
            "WHERE usage_date >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY)")
    int countActiveUsers(@Param("days") Integer days);

    /**
     * 查询使用趋势
     */
    @Select("SELECT usage_date as date, " +
            "SUM(usage_count) as totalUsage, " +
            "COUNT(DISTINCT user_id) as activeUsers " +
            "FROM user_usage_log " +
            "WHERE usage_date >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) " +
            "GROUP BY usage_date " +
            "ORDER BY usage_date")
    List<Map<String, Object>> selectUsageTrend(@Param("days") Integer days);

    /**
     * 查询用户某天的使用次数
     */
    @Select("SELECT COALESCE(SUM(usage_count), 0) FROM user_usage_log " +
            "WHERE user_id = #{userId} AND usage_date = #{date}")
    int selectDailyUsage(@Param("userId") Long userId, @Param("date") String date);
}
