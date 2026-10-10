package com.lingshu.zhikang.service.impl;

import com.lingshu.zhikang.entity.po.CommunityPost;
import com.lingshu.zhikang.mapper.CommunityMapper;
import com.lingshu.zhikang.service.ICommunityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 健康社区服务实现
 */
@Slf4j
@Service
public class CommunityServiceImpl implements ICommunityService {

    @Autowired
    private CommunityMapper communityMapper;

    @Override
    public CommunityPost createPost(CommunityPost post) {
        log.info("创建帖子 - 用户: {}, 标题: {}", post.getUserId(), post.getTitle());

        // 设置默认值
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setCollectCount(0);
        post.setIsTop(false);
        post.setIsEssence(false);
        post.setStatus("published");

        communityMapper.createPost(post);
        return post;
    }

    @Override
    public CommunityPost updatePost(CommunityPost post) {
        log.info("更新帖子 - ID: {}", post.getId());
        communityMapper.updatePost(post);
        return communityMapper.getPostById(post.getId());
    }

    @Override
    public boolean deletePost(Long postId, Long userId) {
        log.info("删除帖子 - ID: {}, 用户: {}", postId, userId);

        // 验证帖子所属
        CommunityPost post = communityMapper.getPostById(postId);
        if (post == null || !post.getUserId().equals(userId)) {
            return false;
        }

        return communityMapper.deletePost(postId) > 0;
    }

    @Override
    public CommunityPost getPostById(Long id) {
        CommunityPost post = communityMapper.getPostById(id);
        if (post != null) {
            // 增加浏览次数
            communityMapper.incrementViewCount(id);
        }
        return post;
    }

    @Override
    public List<CommunityPost> getPostList(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return communityMapper.getPostList(offset, pageSize);
    }

    @Override
    public List<CommunityPost> getPostsByCategory(String category, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return communityMapper.getPostsByCategory(category, offset, pageSize);
    }

    @Override
    public List<CommunityPost> getPostsByDiseaseTag(String diseaseTag, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return communityMapper.getPostsByDiseaseTag(diseaseTag, offset, pageSize);
    }

    @Override
    public List<CommunityPost> getUserPosts(Long userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return communityMapper.getUserPosts(userId, offset, pageSize);
    }

    @Override
    public List<CommunityPost> searchPosts(String keyword, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return communityMapper.searchPosts(keyword, offset, pageSize);
    }

    @Override
    public boolean likePost(Long postId, Long userId) {
        log.info("点赞帖子 - 帖子: {}, 用户: {}", postId, userId);
        // TODO: 检查是否已点赞，记录到点赞表
        return communityMapper.incrementLikeCount(postId) > 0;
    }

    @Override
    public boolean unlikePost(Long postId, Long userId) {
        log.info("取消点赞 - 帖子: {}, 用户: {}", postId, userId);
        // TODO: 从点赞表删除记录
        return communityMapper.decrementLikeCount(postId) > 0;
    }

    @Override
    public boolean collectPost(Long postId, Long userId) {
        log.info("收藏帖子 - 帖子: {}, 用户: {}", postId, userId);
        // TODO: 记录到收藏表
        return communityMapper.incrementCollectCount(postId) > 0;
    }

    @Override
    public boolean uncollectPost(Long postId, Long userId) {
        log.info("取消收藏 - 帖子: {}, 用户: {}", postId, userId);
        // TODO: 从收藏表删除记录
        return communityMapper.decrementCollectCount(postId) > 0;
    }

    @Override
    public List<CommunityPost> getHotPosts(int limit) {
        return communityMapper.getHotPosts(limit);
    }

    @Override
    public List<CommunityPost> getEssencePosts(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return communityMapper.getEssencePosts(offset, pageSize);
    }

    @Override
    public Map<String, Object> getCommunityStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // TODO: 实现统计逻辑
        statistics.put("totalPosts", 0);
        statistics.put("totalUsers", 0);
        statistics.put("todayPosts", 0);
        statistics.put("activeUsers", 0);

        return statistics;
    }
}

