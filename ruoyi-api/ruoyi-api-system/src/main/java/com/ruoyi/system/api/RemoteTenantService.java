package com.ruoyi.system.api;

import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.common.core.dto.TenantNotifyDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户服务
 *
 * @author Lion Li
 */
public interface RemoteTenantService {
    /**
     * 获取上层租户信息
     *
     * @param id
     * @return
     */
    TenantDto getChannel(Long id);
    /**
     * 获取商户信息
     * @param id
     * @return
     */
    TenantDto getTenant(Long id);

    List<TenantDto> getTenants(List<Long> id);

    /**
     * 根据商户号获取商户信息
     *
     * @param mchNo
     * @return
     */
    TenantDto getTenantByNo(String mchNo);

    /**
     * 添加商户余额
     * @param tenantId
     * @param realAmount
     */
    void balanceIncrease(Long tenantId, BigDecimal realAmount);

    /**
     * 减少余额
     * @param tenantId
     * @param realAmount
     */
    void balanceReduce(Long tenantId, BigDecimal realAmount);

    /**
     * 冻结金额
     *
     * @param tenantId
     * @param realAmount
     */
    void balanceFreeze(Long tenantId, BigDecimal realAmount);
    


    TenantNotifyDto findNotifyByOrderId(Long id);

    void saveNotify(TenantNotifyDto mchNotifyRecord);

    TenantNotifyDto getNotifyById(Long notifyId);

    void updateNotifyResult(Long notifyId, int stateSuccess, String res);

    List<TenantDto> getAllTenant();

    /**
     * 获取直属下级
     *
     * @return
     */
    List<Long> getChildTenant(Long tenantId);

    /**
     * 获取下级包含自身
     *
     * @param tenantId
     * @return
     */
    List<Long> getChildTenantAndSelf(Long tenantId);

    /**
     * 根据chatID获取商户信息
     *
     * @param chatId
     * @return
     */
    TenantDto getTenantByChatId(Long chatId);

    /**
     * 冻结金额释放,余额减少
     *
     * @param id
     * @param amount
     */
    void balanceRelease(Long id, BigDecimal amount);

    TenantDto getTenantByDomain(String host);
}
