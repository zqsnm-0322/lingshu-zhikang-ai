package com.lingshu.zhikang.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 慢性病记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("chronic_disease_record")
public class ChronicDiseaseRecord {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 指标类型: blood_pressure(血压), blood_sugar(血糖), weight(体重), heart_rate(心率)
     */
    private String type;
    
    /**
     * 记录日期
     */
    private String date;
    
    /**
     * 记录时间
     */
    private String time;
    
    /**
     * 收缩压 (仅血压类型使用)
     */
    private Integer systolic;
    
    /**
     * 舒张压 (仅血压类型使用)
     */
    private Integer diastolic;
    
    /**
     * 通用数值 (血糖、体重、心率等使用)
     */
    private Double value;
    
    /**
     * 用餐类型 (仅血糖类型使用): 空腹、餐后1小时、餐后2小时、睡前
     */
    private String mealType;
    
    /**
     * 备注信息
     */
    private String notes;
    
    /**
     * 是否异常
     */
    private Boolean isAbnormal;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

