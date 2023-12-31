package com.ruoyi.system.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.system.domain.SysTenant;
import com.ruoyi.system.domain.vo.SysTenantVo;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 租户Mapper接口
 *
 * @author ruoyi
 * @date 2023-11-20
 */
public interface SysTenantMapper extends BaseMapperPlus<SysTenantMapper, SysTenant, SysTenantVo> {

    /**
     * 查询直属下级商户
     *
     * @param tenantId
     * @return
     */
    @Select("select id,name from sys_tenant where tenant_id=#{tenantId}")
    List<SysTenant> selectChildList(Long tenantId);
}
