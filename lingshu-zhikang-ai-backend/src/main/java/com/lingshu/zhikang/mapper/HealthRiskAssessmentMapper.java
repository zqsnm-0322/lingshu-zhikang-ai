package com.lingshu.zhikang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingshu.zhikang.entity.po.HealthRiskAssessment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 健康风险评估Mapper接口
 */
@Mapper
public interface HealthRiskAssessmentMapper extends BaseMapper<HealthRiskAssessment> {
    
    /**
     * 根据用户ID查询评估记录
     */
    @Select("SELECT * FROM health_risk_assessment WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<HealthRiskAssessment> selectByUserId(Long userId);
    
    /**
     * 根据企业ID查询评估记录
     */
    @Select("SELECT * FROM health_risk_assessment WHERE enterprise_id = #{enterpriseId} ORDER BY created_at DESC")
    List<HealthRiskAssessment> selectByEnterpriseId(Long enterpriseId);
    
    /**
     * 查询用户最新的评估记录
     */
    @Select("SELECT * FROM health_risk_assessment WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT 1")
    HealthRiskAssessment selectLatestByUserId(Long userId);
}
