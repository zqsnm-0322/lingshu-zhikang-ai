package com.lingshu.zhikang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingshu.zhikang.entity.po.ChronicDiseaseRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 慢性病记录 Mapper 接口
 */
@Mapper
public interface ChronicDiseaseMapper extends BaseMapper<ChronicDiseaseRecord> {
    
    /**
     * 根据用户ID和类型查询记录
     */
    @Select("SELECT * FROM chronic_disease_record WHERE user_id = #{userId} AND type = #{type} ORDER BY date DESC, time DESC")
    List<ChronicDiseaseRecord> selectByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);
    
    /**
     * 根据用户ID查询所有记录
     */
    @Select("SELECT * FROM chronic_disease_record WHERE user_id = #{userId} ORDER BY date DESC, time DESC")
    List<ChronicDiseaseRecord> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户某个指标类型的统计信息
     */
    @Select("SELECT " +
            "COUNT(*) as totalRecords, " +
            "SUM(CASE WHEN is_abnormal = 1 THEN 1 ELSE 0 END) as abnormalCount " +
            "FROM chronic_disease_record " +
            "WHERE user_id = #{userId} AND type = #{type}")
    Map<String, Object> selectStatsByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);
    
    /**
     * 根据用户ID和日期查询记录
     */
    @Select("SELECT * FROM chronic_disease_record WHERE user_id = #{userId} AND date = #{date} " +
            "ORDER BY time DESC")
    List<ChronicDiseaseRecord> getByUserIdAndDate(@Param("userId") Long userId, @Param("date") String date);
    
    /**
     * 根据用户ID和日期范围查询记录
     */
    @Select("SELECT * FROM chronic_disease_record WHERE user_id = #{userId} " +
            "AND date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY date DESC, time DESC")
    List<ChronicDiseaseRecord> getByUserIdAndDateRange(@Param("userId") Long userId, 
                                                        @Param("startDate") String startDate, 
                                                        @Param("endDate") String endDate);
    
    /**
     * 查询用户最近N条记录
     */
    @Select("SELECT * FROM chronic_disease_record WHERE user_id = #{userId} " +
            "ORDER BY date DESC, time DESC LIMIT #{limit}")
    List<ChronicDiseaseRecord> getRecentRecords(@Param("userId") Long userId, @Param("limit") int limit);
}

