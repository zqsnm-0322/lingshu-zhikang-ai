package com.lingshu.zhikang.mapper;

import com.lingshu.zhikang.entity.po.RechargeOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 充值订单Mapper
 */
@Mapper
public interface RechargeOrderMapper {

    /**
     * 插入订单
     */
    @Insert("INSERT INTO recharge_order (order_no, user_id, target_level, payment_type, amount, payment_status, expire_at) " +
            "VALUES (#{orderNo}, #{userId}, #{targetLevel}, #{paymentType}, #{amount}, #{paymentStatus}, #{expireAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RechargeOrder order);

    /**
     * 根据订单号查询
     */
    @Select("SELECT * FROM recharge_order WHERE order_no = #{orderNo}")
    RechargeOrder selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据用户ID查询订单列表
     */
    @Select("SELECT * FROM recharge_order WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<RechargeOrder> selectByUserId(@Param("userId") Long userId);

    /**
     * 更新订单
     */
    @Update("UPDATE recharge_order SET " +
            "payment_method = #{paymentMethod}, " +
            "payment_status = #{paymentStatus}, " +
            "paid_at = #{paidAt}, " +
            "updated_at = NOW() " +
            "WHERE id = #{id}")
    int updateById(RechargeOrder order);

    /**
     * 查询会员等级配置列表
     */
    @Select("SELECT * FROM member_level_config WHERE is_active = 1 ORDER BY sort_order")
    List<Map<String, Object>> selectMemberLevelConfigs();

    /**
     * 查询指定会员等级配置
     */
    @Select("SELECT * FROM member_level_config WHERE level_code = #{levelCode} AND is_active = 1")
    Map<String, Object> selectMemberLevelConfig(@Param("levelCode") String levelCode);
}
