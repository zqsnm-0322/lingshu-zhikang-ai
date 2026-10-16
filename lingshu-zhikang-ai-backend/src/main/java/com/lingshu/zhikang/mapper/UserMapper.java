package com.lingshu.zhikang.mapper;

import com.lingshu.zhikang.entity.po.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    
    /**
     * 根据用户名查找用户
     */
    User findByUsername(@Param("username") String username);
    
    /**
     * 插入用户
     */
    void insertUser(User user);
    
    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(@Param("id") Long id);
    
    /**
     * 更新用户信息
     */
    @Update("UPDATE user SET " +
            "username = #{username}, " +
            "role = #{role}, " +
            "email = #{email}, " +
            "phone = #{phone}, " +
            "avatar = #{avatar}, " +
            "member_level = #{memberLevel}, " +
            "daily_quota = #{dailyQuota}, " +
            "daily_used = #{dailyUsed}, " +
            "total_used = #{totalUsed}, " +
            "last_use_date = #{lastUseDate}, " +
            "last_login_time = #{lastLoginTime}, " +
            "status = #{status}, " +
            "updated_at = NOW() " +
            "WHERE id = #{id}")
    int updateById(User user);

    /**
     * 更新用户会员等级和配额
     */
    @Update("UPDATE user SET " +
            "member_level = #{memberLevel}, " +
            "daily_quota = #{dailyQuota}, " +
            "updated_at = NOW() " +
            "WHERE id = #{id}")
    int updateMemberLevel(@Param("id") Long id, @Param("memberLevel") String memberLevel, @Param("dailyQuota") Integer dailyQuota);
    
    /**
     * 查询用户列表(分页)
     */
    @Select("<script>" +
            "SELECT * FROM user WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (username LIKE CONCAT('%', #{keyword}, '%') OR email LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "<if test='memberLevel != null and memberLevel != \"\"'>" +
            "AND member_level = #{memberLevel} " +
            "</if>" +
            "<if test='status != null and status != \"\"'>" +
            "AND status = #{status} " +
            "</if>" +
            "ORDER BY register_time DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<User> selectUserList(
            @Param("keyword") String keyword,
            @Param("memberLevel") String memberLevel,
            @Param("status") String status,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize
    );
    
    /**
     * 统计用户数量
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM user WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (username LIKE CONCAT('%', #{keyword}, '%') OR email LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "<if test='memberLevel != null and memberLevel != \"\"'>" +
            "AND member_level = #{memberLevel} " +
            "</if>" +
            "<if test='status != null and status != \"\"'>" +
            "AND status = #{status} " +
            "</if>" +
            "</script>")
    int countUsers(
            @Param("keyword") String keyword,
            @Param("memberLevel") String memberLevel,
            @Param("status") String status
    );
    
    /**
     * 统计今日新增用户
     */
    @Select("SELECT COUNT(*) FROM user WHERE DATE(register_time) = CURDATE()")
    int countTodayNewUsers();
    
    /**
     * 在一次受保护请求成功完成后，更新用户每日使用次数。
     */
    @Update("UPDATE user SET " +
            "daily_used = daily_used + 1, " +
            "total_used = total_used + 1, " +
            "last_use_date = CURDATE() " +
            "WHERE id = #{userId}")
    int incrementUsage(@Param("userId") Long userId);
    
    /**
     * 重置每日使用次数(定时任务调用)
     */
    @Update("UPDATE user SET daily_used = 0 WHERE last_use_date < CURDATE()")
    int resetDailyUsage();
    
    /**
     * 检查用户是否还有配额
     */
    @Select("SELECT (daily_quota - daily_used) > 0 FROM user WHERE id = #{userId}")
    boolean hasQuota(@Param("userId") Long userId);
}
