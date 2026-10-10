package com.lingshu.zhikang.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 社区帖子实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPost {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 发帖用户ID
     */
    private Long userId;

    /**
     * 分类：经验分享/求助问答/病友日记/健康资讯
     */
    private String category;

    /**
     * 疾病标签：高血压/糖尿病等
     */
    private String diseaseTag;

    /**
     * 帖子标题
     */
    private String title;

    /**
     * 帖子内容
     */
    private String content;

    /**
     * 图片URL列表(JSON格式)
     */
    private String images;

    /**
     * 是否匿名发帖
     */
    private Boolean isAnonymous;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 收藏数
     */
    private Integer collectCount;

    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 是否精华
     */
    private Boolean isEssence;

    /**
     * 状态：draft/published/deleted/blocked
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}

