package com.lingshu.zhikang.service;

import com.lingshu.zhikang.entity.po.CommunityPost;

import java.util.List;
import java.util.Map;

/**
 * 健康社区服务接口
 */
public interface ICommunityService {

    /**
     * 创建帖子
     */
    CommunityPost createPost(CommunityPost post);

    /**
     * 更新帖子
     */
    CommunityPost updatePost(CommunityPost post);

    /**
     * 删除帖子
     */
    boolean deletePost(Long postId, Long userId);

    /**
     * 获取帖子详情
     */
    CommunityPost getPostById(Long id);

    /**
     * 获取帖子列表（分页）
     */
    List<CommunityPost> getPostList(int page, int pageSize);

    /**
     * 按分类获取帖子列表
     */
    List<CommunityPost> getPostsByCategory(String category, int page, int pageSize);

    /**
     * 按疾病标签获取帖子列表
     */
    List<CommunityPost> getPostsByDiseaseTag(String diseaseTag, int page, int pageSize);

    /**
     * 获取用户发布的帖子
     */
    List<CommunityPost> getUserPosts(Long userId, int page, int pageSize);

    /**
     * 搜索帖子
     */
    List<CommunityPost> searchPosts(String keyword, int page, int pageSize);

    /**
     * 点赞帖子
     */
    boolean likePost(Long postId, Long userId);

    /**
     * 取消点赞
     */
    boolean unlikePost(Long postId, Long userId);

    /**
     * 收藏帖子
     */
    boolean collectPost(Long postId, Long userId);

    /**
     * 取消收藏
     */
    boolean uncollectPost(Long postId, Long userId);

    /**
     * 获取热门帖子
     */
    List<CommunityPost> getHotPosts(int limit);

    /**
     * 获取精华帖子
     */
    List<CommunityPost> getEssencePosts(int page, int pageSize);

    /**
     * 获取社区统计数据
     */
    Map<String, Object> getCommunityStatistics();
}

