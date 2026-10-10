package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.entity.po.CommunityPost;
import com.lingshu.zhikang.entity.vo.ResponseResult;
import com.lingshu.zhikang.service.ICommunityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 健康社区控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/community")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class CommunityController {

    @Autowired
    private ICommunityService communityService;

    /**
     * 创建帖子
     */
    @PostMapping("/post")
    public ResponseResult<CommunityPost> createPost(@RequestBody CommunityPost post) {
        try {
            CommunityPost created = communityService.createPost(post);
            return ResponseResult.ok(created);
        } catch (Exception e) {
            log.error("创建帖子失败", e);
            return ResponseResult.error("创建帖子失败: " + e.getMessage());
        }
    }

    /**
     * 更新帖子
     */
    @PutMapping("/post")
    public ResponseResult<CommunityPost> updatePost(@RequestBody CommunityPost post) {
        try {
            CommunityPost updated = communityService.updatePost(post);
            return ResponseResult.ok(updated);
        } catch (Exception e) {
            log.error("更新帖子失败", e);
            return ResponseResult.error("更新帖子失败: " + e.getMessage());
        }
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/post/{id}")
    public ResponseResult<Boolean> deletePost(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            boolean success = communityService.deletePost(id, userId);
            return ResponseResult.ok(success);
        } catch (Exception e) {
            log.error("删除帖子失败", e);
            return ResponseResult.error("删除帖子失败: " + e.getMessage());
        }
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/post/{id}")
    public ResponseResult<CommunityPost> getPostById(@PathVariable Long id) {
        try {
            CommunityPost post = communityService.getPostById(id);
            return ResponseResult.ok(post);
        } catch (Exception e) {
            log.error("获取帖子详情失败", e);
            return ResponseResult.error("获取帖子详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取帖子列表
     */
    @GetMapping("/post/list")
    public ResponseResult<List<CommunityPost>> getPostList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            List<CommunityPost> posts = communityService.getPostList(page, pageSize);
            return ResponseResult.ok(posts);
        } catch (Exception e) {
            log.error("获取帖子列表失败", e);
            return ResponseResult.error("获取帖子列表失败: " + e.getMessage());
        }
    }

    /**
     * 按分类获取帖子
     */
    @GetMapping("/post/category/{category}")
    public ResponseResult<List<CommunityPost>> getPostsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            List<CommunityPost> posts = communityService.getPostsByCategory(category, page, pageSize);
            return ResponseResult.ok(posts);
        } catch (Exception e) {
            log.error("按分类获取帖子失败", e);
            return ResponseResult.error("按分类获取帖子失败: " + e.getMessage());
        }
    }

    /**
     * 按疾病标签获取帖子
     */
    @GetMapping("/post/disease/{diseaseTag}")
    public ResponseResult<List<CommunityPost>> getPostsByDiseaseTag(
            @PathVariable String diseaseTag,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            List<CommunityPost> posts = communityService.getPostsByDiseaseTag(diseaseTag, page, pageSize);
            return ResponseResult.ok(posts);
        } catch (Exception e) {
            log.error("按疾病标签获取帖子失败", e);
            return ResponseResult.error("按疾病标签获取帖子失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的帖子
     */
    @GetMapping("/post/user/{userId}")
    public ResponseResult<List<CommunityPost>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            List<CommunityPost> posts = communityService.getUserPosts(userId, page, pageSize);
            return ResponseResult.ok(posts);
        } catch (Exception e) {
            log.error("获取用户帖子失败", e);
            return ResponseResult.error("获取用户帖子失败: " + e.getMessage());
        }
    }

    /**
     * 搜索帖子
     */
    @GetMapping("/post/search")
    public ResponseResult<List<CommunityPost>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            List<CommunityPost> posts = communityService.searchPosts(keyword, page, pageSize);
            return ResponseResult.ok(posts);
        } catch (Exception e) {
            log.error("搜索帖子失败", e);
            return ResponseResult.error("搜索帖子失败: " + e.getMessage());
        }
    }

    /**
     * 点赞帖子
     */
    @PostMapping("/post/{id}/like")
    public ResponseResult<Boolean> likePost(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            boolean success = communityService.likePost(id, userId);
            return ResponseResult.ok(success);
        } catch (Exception e) {
            log.error("点赞帖子失败", e);
            return ResponseResult.error("点赞帖子失败: " + e.getMessage());
        }
    }

    /**
     * 取消点赞
     */
    @DeleteMapping("/post/{id}/like")
    public ResponseResult<Boolean> unlikePost(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            boolean success = communityService.unlikePost(id, userId);
            return ResponseResult.ok(success);
        } catch (Exception e) {
            log.error("取消点赞失败", e);
            return ResponseResult.error("取消点赞失败: " + e.getMessage());
        }
    }

    /**
     * 收藏帖子
     */
    @PostMapping("/post/{id}/collect")
    public ResponseResult<Boolean> collectPost(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            boolean success = communityService.collectPost(id, userId);
            return ResponseResult.ok(success);
        } catch (Exception e) {
            log.error("收藏帖子失败", e);
            return ResponseResult.error("收藏帖子失败: " + e.getMessage());
        }
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/post/{id}/collect")
    public ResponseResult<Boolean> uncollectPost(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            boolean success = communityService.uncollectPost(id, userId);
            return ResponseResult.ok(success);
        } catch (Exception e) {
            log.error("取消收藏失败", e);
            return ResponseResult.error("取消收藏失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门帖子
     */
    @GetMapping("/post/hot")
    public ResponseResult<List<CommunityPost>> getHotPosts(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<CommunityPost> posts = communityService.getHotPosts(limit);
            return ResponseResult.ok(posts);
        } catch (Exception e) {
            log.error("获取热门帖子失败", e);
            return ResponseResult.error("获取热门帖子失败: " + e.getMessage());
        }
    }

    /**
     * 获取精华帖子
     */
    @GetMapping("/post/essence")
    public ResponseResult<List<CommunityPost>> getEssencePosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            List<CommunityPost> posts = communityService.getEssencePosts(page, pageSize);
            return ResponseResult.ok(posts);
        } catch (Exception e) {
            log.error("获取精华帖子失败", e);
            return ResponseResult.error("获取精华帖子失败: " + e.getMessage());
        }
    }

    /**
     * 获取社区统计数据
     */
    @GetMapping("/statistics")
    public ResponseResult<Map<String, Object>> getCommunityStatistics() {
        try {
            Map<String, Object> statistics = communityService.getCommunityStatistics();
            return ResponseResult.ok(statistics);
        } catch (Exception e) {
            log.error("获取社区统计数据失败", e);
            return ResponseResult.error("获取社区统计数据失败: " + e.getMessage());
        }
    }
}

