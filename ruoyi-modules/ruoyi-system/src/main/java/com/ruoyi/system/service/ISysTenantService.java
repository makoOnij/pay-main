package com.ruoyi.system.service;

import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.system.domain.SysTenant;
import com.ruoyi.system.domain.bo.SysTenantBo;
import com.ruoyi.system.domain.bo.SysTenantChargeDto;
import com.ruoyi.system.domain.dto.BatchTenantDto;
import com.ruoyi.system.domain.dto.UpdateTenantSecurityDto;
import com.ruoyi.system.domain.vo.SysTenantVo;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * 租户Service接口
 *
 * @author ruoyi
 * @date 2023-11-20
 */
public interface ISysTenantService {

    /**
     * 查询租户
     */
    SysTenantVo queryById(Long id);

    /**
     * 查询租户列表
     */
    TableDataInfo<SysTenantVo> queryPageList(SysTenantBo bo, PageQuery pageQuery);

    /**
     * 查询租户列表
     */
    List<SysTenantVo> queryList(SysTenantBo bo);

    /**
     * 修改租户
     */
    Boolean insertByBo(SysTenantBo bo);

    /**
     * 修改租户
     */
    Boolean updateByBo(SysTenantBo bo);

    /**
     * 校验并批量删除租户信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 商户充值
     *
     * @param bo
     * @return
     */
    int amountIncrease(SysTenantChargeDto bo);

    /**
     * 修改租户状态
     *
     * @param user
     * @return
     */
    int updateStatus(SysTenant user);

    /**
     * 批量更新充值汇率
     *
     * @param ids
     * @param whiteList
     * @return
     */
    int batchUpdateWhiteList(List<Long> ids, String whiteList);

    /**
     * 批量更新状态
     *
     * @param ids
     * @param status
     * @return
     */
    int batchUpdateStatus(List<Long> ids, Integer status);

    /**
     * 批量修改内冲汇率
     *
     * @return
     */
    int batchUpdateChargeRate(BatchTenantDto dto);

    /**
     * 批量更新接口白名单
     *
     * @param ids
     * @param apiWhiteList
     * @return
     */
    int batchUpdateApiWhiteList(List<Long> ids, String apiWhiteList);

    /**
     * 修改商户安全信息
     *
     * @param bo
     * @return
     */
    int updateSecurity(UpdateTenantSecurityDto bo);

    /**
     * 更新自动代付
     *
     * @param bo
     * @return
     */
    int updateAutoPay(UpdateTenantSecurityDto bo);

    List<TenantDto> getChilds(Long tenantId);

    BigDecimal getChargeRate(Long tenantId);

    BigDecimal getWithdrawRate(Long tenantId);

    String getChargeAddress(Long tenantId);

    void balanceRelease(Long tenantId, BigDecimal amount);

    void balanceFreeze(Long tenantId, BigDecimal realAmount);

    void balanceIncrease(Long tenantId, BigDecimal realAmount);

    int updateApi(SysTenantBo bo);

    void balanceReduce(Long tenantId, BigDecimal realAmount);

    TenantDto getByDomain(String host);
}
