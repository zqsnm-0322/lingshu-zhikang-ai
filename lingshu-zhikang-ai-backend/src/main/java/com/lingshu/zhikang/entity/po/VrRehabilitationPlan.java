package com.lingshu.zhikang.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * VR康复训练计划实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("vr_rehabilitation_plan")
public class VrRehabilitationPlan {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 计划名称
     */
    private String planName;
    
    /**
     * 疾病类型
     */
    private String diseaseType;
    
    /**
     * 难度等级：beginner-初级, intermediate-中级, advanced-高级
     */
    private String difficultyLevel;
    
    /**
     * 计划周期（周）
     */
    private Integer durationWeeks;
    
    /**
     * 计划描述
     */
    private String description;
    
    /**
     * 目标肌肉群（JSON数组）
     */
    private String targetMuscleGroups;
    
    /**
     * 训练项目列表（JSON数组）
     */
    private String exercises;
    
    /**
     * VR场景URL
     */
    private String vrSceneUrl;
    
    /**
     * 注意事项
     */
    private String precautions;
    
    /**
     * 预期效果
     */
    private String expectedResults;
    
    /**
     * 医生建议
     */
    private String doctorAdvice;
    
    /**
     * 是否启用
     */
    private Boolean isActive;
    
    /**
     * 创建人（医生ID）
     */
    private Long createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}

