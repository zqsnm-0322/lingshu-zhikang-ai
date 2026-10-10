package com.lingshu.zhikang.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 服药记录实体类
 */
@Data
@TableName("medicine_record")
public class MedicineRecord {
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
     * 用药提醒ID
     */
    private Long reminderId;
    
    /**
     * 记录日期
     */
    private LocalDate recordDate;
    
    /**
     * 提醒时间
     */
    private String reminderTime;
    
    /**
     * 状态：pending-待服用，completed-已完成，skipped-已跳过，overdue-已过期
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

