package com.lingshu.zhikang.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 健康日记实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("health_diary")
public class HealthDiary {
    
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
     * 日记日期
     */
    private String date;
    
    /**
     * 心情等级 (1-5)
     */
    private Integer mood;
    
    /**
     * 睡眠时长 (小时)
     */
    private Double sleep;
    
    /**
     * 运动情况
     */
    private String exercise;
    
    /**
     * 饮食情况
     */
    private String diet;
    
    /**
     * 症状描述
     */
    private String symptoms;
    
    /**
     * 备注
     */
    private String notes;
    
    /**
     * 标签 (逗号分隔)
     */
    private String tags;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

