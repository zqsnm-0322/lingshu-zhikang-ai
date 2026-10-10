package com.lingshu.zhikang.entity.po;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 人体器官模型实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("human_organ_model")
public class HumanOrganModel {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 器官名称（中文）
     */
    private String organName;
    
    /**
     * 器官名称（英文）
     */
    private String organNameEn;
    
    /**
     * 所属系统
     * circulatory-循环系统, respiratory-呼吸系统, digestive-消化系统
     * nervous-神经系统, skeletal-骨骼系统, muscular-肌肉系统
     * endocrine-内分泌系统, urinary-泌尿系统, reproductive-生殖系统, immune-免疫系统
     */
    private String bodySystem;
    
    /**
     * 3D模型URL
     */
    private String modelUrl;
    
    /**
     * 器官位置坐标（JSON格式: {"x": 0, "y": 0, "z": 0}）
     */
    private String position;
    
    /**
     * 器官功能描述
     */
    private String functionDesc;
    
    /**
     * 常见疾病列表（JSON数组）
     */
    private String commonDiseases;
    
    /**
     * 健康建议（JSON数组）
     */
    private String healthTips;
    
    /**
     * 交互热点（JSON格式）
     */
    private String interactivePoints;
    
    /**
     * 动画URL列表（JSON数组）
     */
    private String animationUrls;
    
    /**
     * AR识别标记
     */
    private String arMarker;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}

