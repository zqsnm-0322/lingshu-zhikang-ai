package com.lingshu.zhikang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingshu.zhikang.entity.po.EnterpriseCustomer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 企业客户Mapper接口
 */
@Mapper
public interface EnterpriseCustomerMapper extends BaseMapper<EnterpriseCustomer> {
    
    /**
     * 根据API密钥查找企业客户
     */
    @Select("SELECT * FROM enterprise_customer WHERE api_key = #{apiKey} AND status = 'ACTIVE'")
    EnterpriseCustomer selectByApiKey(String apiKey);
}
