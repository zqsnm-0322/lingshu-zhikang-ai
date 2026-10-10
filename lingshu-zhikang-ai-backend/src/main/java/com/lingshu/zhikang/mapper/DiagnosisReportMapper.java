package com.lingshu.zhikang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingshu.zhikang.entity.po.DiagnosisReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI诊断报告Mapper接口
 */
@Mapper
public interface DiagnosisReportMapper extends BaseMapper<DiagnosisReport> {
    
    /**
     * 获取用户的诊断报告列表
     */
    @Select("SELECT * FROM diagnosis_report WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<DiagnosisReport> getReportsByUserId(Long userId);
}
