package com.lingshu.zhikang.mapper;

import com.lingshu.zhikang.entity.po.HealthStatistics;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 健康数据统计Mapper
 */
@Mapper
public interface HealthStatisticsMapper {

    /**
     * 创建或更新统计数据
     */
    @Insert("INSERT INTO health_statistics (user_id, stat_date, stat_type, health_score, " +
            "blood_pressure_avg, blood_sugar_avg, weight_avg, heart_rate_avg, sleep_hours_avg, " +
            "exercise_minutes_total, medication_compliance, abnormal_count, data_summary) " +
            "VALUES (#{userId}, #{statDate}, #{statType}, #{healthScore}, #{bloodPressureAvg}, " +
            "#{bloodSugarAvg}, #{weightAvg}, #{heartRateAvg}, #{sleepHoursAvg}, " +
            "#{exerciseMinutesTotal}, #{medicationCompliance}, #{abnormalCount}, #{dataSummary}) " +
            "ON DUPLICATE KEY UPDATE health_score=#{healthScore}, blood_pressure_avg=#{bloodPressureAvg}, " +
            "blood_sugar_avg=#{bloodSugarAvg}, weight_avg=#{weightAvg}, heart_rate_avg=#{heartRateAvg}, " +
            "sleep_hours_avg=#{sleepHoursAvg}, exercise_minutes_total=#{exerciseMinutesTotal}, " +
            "medication_compliance=#{medicationCompliance}, abnormal_count=#{abnormalCount}, " +
            "data_summary=#{dataSummary}")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int saveOrUpdateStatistics(HealthStatistics statistics);

    /**
     * 获取用户指定日期和类型的统计数据
     */
    @Select("SELECT * FROM health_statistics WHERE user_id = #{userId} AND stat_date = #{statDate} " +
            "AND stat_type = #{statType}")
    HealthStatistics getStatistics(@Param("userId") Long userId, @Param("statDate") LocalDate statDate,
                                   @Param("statType") String statType);

    /**
     * 获取用户指定时间范围的统计数据
     */
    @Select("SELECT * FROM health_statistics WHERE user_id = #{userId} AND stat_type = #{statType} " +
            "AND stat_date BETWEEN #{startDate} AND #{endDate} ORDER BY stat_date ASC")
    List<HealthStatistics> getStatisticsByDateRange(@Param("userId") Long userId,
                                                    @Param("statType") String statType,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

    /**
     * 获取用户最新的统计数据
     */
    @Select("SELECT * FROM health_statistics WHERE user_id = #{userId} AND stat_type = #{statType} " +
            "ORDER BY stat_date DESC LIMIT 1")
    HealthStatistics getLatestStatistics(@Param("userId") Long userId, @Param("statType") String statType);

    /**
     * 获取用户最近N条统计记录
     */
    @Select("SELECT * FROM health_statistics WHERE user_id = #{userId} AND stat_type = #{statType} " +
            "ORDER BY stat_date DESC LIMIT #{limit}")
    List<HealthStatistics> getRecentStatistics(@Param("userId") Long userId,
                                               @Param("statType") String statType,
                                               @Param("limit") int limit);
}

