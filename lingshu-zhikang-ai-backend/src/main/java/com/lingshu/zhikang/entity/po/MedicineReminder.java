package com.lingshu.zhikang.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用药提醒实体类
 */
@Data
@TableName("medicine_reminder")
public class MedicineReminder {
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
     * 药品名称
     */
    private String name;
    
    /**
     * 用药剂量
     */
    private String dosage;
    
    /**
     * 服用频率
     */
    private String frequency;
    
    /**
     * 提醒时间列表（JSON格式存储）
     */
    private String reminderTimes;
    
    /**
     * 开始日期
     */
    private LocalDate startDate;
    
    /**
     * 结束日期
     */
    private LocalDate endDate;
    
    /**
     * 备注说明
     */
    private String notes;
    
    /**
     * 是否启用
     */
    private Boolean isActive;
    
    /**
     * 显示颜色
     */
    private String color;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

