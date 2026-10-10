package com.lingshu.zhikang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingshu.zhikang.entity.po.MemberSubscription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 会员订阅Mapper接口
 */
@Mapper
public interface MemberSubscriptionMapper extends BaseMapper<MemberSubscription> {
    
    /**
     * 根据用户ID获取当前有效的会员订阅
     */
    @Select("SELECT * FROM member_subscription WHERE user_id = #{userId} AND status = 'ACTIVE' " +
            "AND end_date >= CURDATE() ORDER BY end_date DESC LIMIT 1")
    MemberSubscription selectActiveSubscription(Long userId);
}
