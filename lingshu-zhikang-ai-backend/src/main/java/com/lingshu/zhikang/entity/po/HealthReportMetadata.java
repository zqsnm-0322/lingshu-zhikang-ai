package com.lingshu.zhikang.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("health_report_metadata")
public class HealthReportMetadata {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String fileId;
    private String fileName;
    private String title;
    private String contentType;
    private Long fileSize;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
