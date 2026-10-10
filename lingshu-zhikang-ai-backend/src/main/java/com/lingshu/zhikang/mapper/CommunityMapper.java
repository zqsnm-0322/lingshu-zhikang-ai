package com.lingshu.zhikang.mapper;

import com.lingshu.zhikang.entity.po.CommunityPost;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 健康社区Mapper
 */
@Mapper
public interface CommunityMapper {

    /**
     * 创建帖子
     */
    @Insert("INSERT INTO community_post (user_id, category, disease_tag, title, content, images, " +
            "is_anonymous, status) VALUES (#{userId}, #{category}, #{diseaseTag}, #{title}, " +
            "#{content}, #{images}, #{isAnonymous}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int createPost(CommunityPost post);

    /**
     * 更新帖子
     */
    @Update("UPDATE community_post SET category=#{category}, disease_tag=#{diseaseTag}, " +
            "title=#{title}, content=#{content}, images=#{images}, status=#{status} WHERE id=#{id}")
    int updatePost(CommunityPost post);

    /**
     * 删除帖子（软删除）
     */
    @Update("UPDATE community_post SET status='deleted' WHERE id=#{id}")
    int deletePost(@Param("id") Long id);

    /**
     * 获取帖子详情
     */
    @Select("SELECT * FROM community_post WHERE id = #{id} AND status != 'deleted'")
    CommunityPost getPostById(@Param("id") Long id);

    /**
     * 获取帖子列表（分页）
     */
    @Select("SELECT * FROM community_post WHERE status = 'published' " +
            "ORDER BY is_top DESC, created_time DESC LIMIT #{offset}, #{limit}")
    List<CommunityPost> getPostList(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 按分类获取帖子列表
     */
    @Select("SELECT * FROM community_post WHERE category = #{category} AND status = 'published' " +
            "ORDER BY is_top DESC, created_time DESC LIMIT #{offset}, #{limit}")
    List<CommunityPost> getPostsByCategory(@Param("category") String category,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    /**
     * 按疾病标签获取帖子列表
     */
    @Select("SELECT * FROM community_post WHERE disease_tag = #{diseaseTag} AND status = 'published' " +
            "ORDER BY is_top DESC, created_time DESC LIMIT #{offset}, #{limit}")
    List<CommunityPost> getPostsByDiseaseTag(@Param("diseaseTag") String diseaseTag,
                                             @Param("offset") int offset,
                                             @Param("limit") int limit);

    /**
     * 获取用户发布的帖子
     */
    @Select("SELECT * FROM community_post WHERE user_id = #{userId} AND status != 'deleted' " +
            "ORDER BY created_time DESC LIMIT #{offset}, #{limit}")
    List<CommunityPost> getUserPosts(@Param("userId") Long userId,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);

    /**
     * 搜索帖子
     */
    @Select("SELECT * FROM community_post WHERE (title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR content LIKE CONCAT('%', #{keyword}, '%')) AND status = 'published' " +
            "ORDER BY created_time DESC LIMIT #{offset}, #{limit}")
    List<CommunityPost> searchPosts(@Param("keyword") String keyword,
                                    @Param("offset") int offset,
                                    @Param("limit") int limit);

    /**
     * 增加浏览次数
     */
    @Update("UPDATE community_post SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);

    /**
     * 增加点赞数
     */
    @Update("UPDATE community_post SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(@Param("id") Long id);

    /**
     * 减少点赞数
     */
    @Update("UPDATE community_post SET like_count = like_count - 1 WHERE id = #{id} AND like_count > 0")
    int decrementLikeCount(@Param("id") Long id);

    /**
     * 增加评论数
     */
    @Update("UPDATE community_post SET comment_count = comment_count + 1 WHERE id = #{id}")
    int incrementCommentCount(@Param("id") Long id);

    /**
     * 减少评论数
     */
    @Update("UPDATE community_post SET comment_count = comment_count - 1 WHERE id = #{id} AND comment_count > 0")
    int decrementCommentCount(@Param("id") Long id);

    /**
     * 增加收藏数
     */
    @Update("UPDATE community_post SET collect_count = collect_count + 1 WHERE id = #{id}")
    int incrementCollectCount(@Param("id") Long id);

    /**
     * 减少收藏数
     */
    @Update("UPDATE community_post SET collect_count = collect_count - 1 WHERE id = #{id} AND collect_count > 0")
    int decrementCollectCount(@Param("id") Long id);

    /**
     * 获取热门帖子
     */
    @Select("SELECT * FROM community_post WHERE status = 'published' " +
            "ORDER BY (view_count * 0.3 + like_count * 0.4 + comment_count * 0.3) DESC, created_time DESC " +
            "LIMIT #{limit}")
    List<CommunityPost> getHotPosts(@Param("limit") int limit);

    /**
     * 获取精华帖子
     */
    @Select("SELECT * FROM community_post WHERE is_essence = true AND status = 'published' " +
            "ORDER BY created_time DESC LIMIT #{offset}, #{limit}")
    List<CommunityPost> getEssencePosts(@Param("offset") int offset, @Param("limit") int limit);
}

