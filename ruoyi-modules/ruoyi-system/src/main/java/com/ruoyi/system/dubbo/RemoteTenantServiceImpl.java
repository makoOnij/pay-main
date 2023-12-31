package com.ruoyi.system.dubbo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.common.core.dto.TenantNotifyDto;
import com.ruoyi.common.core.enums.TenantType;
import com.ruoyi.system.api.RemoteTenantService;
import com.ruoyi.system.domain.SysTenant;
import com.ruoyi.system.domain.TenantNotify;
import com.ruoyi.system.mapper.SysTenantMapper;
import com.ruoyi.system.mapper.TenantNotifyMapper;
import com.ruoyi.system.service.ISysTenantService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户服务
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteTenantServiceImpl implements RemoteTenantService {

    private final SysTenantMapper tenantMapper;

    private final TenantNotifyMapper tenantNotifyMapper;

    private final ISysTenantService tenantService;


    /**
     * 获取上层租户信息
     *
     * @param id
     * @return
     */
    @Override
    public TenantDto getChannel(Long id) {
        List<TenantDto> allTenant = getAllTenant().stream().filter(p -> !p.getId().equals(1L)).collect(Collectors.toList());
        TenantDto parent = getParent(allTenant, id);
        return parent;
    }

    private TenantDto getParent(List<TenantDto> list, Long id) {
        Optional<TenantDto> first = list.stream().filter(p -> p.getId().equals(id)).findFirst();
        if (first.isPresent()) {
            TenantDto p = first.get();
            if (p.getTenantId() <= 1L && p.getTenantType().equals(TenantType.CHANNEL.getCode())) {
                return p;
            }
            if (ObjectUtil.isNotEmpty(p.getTenantId())
            ) {
                TenantDto parent = getParent(list, p.getTenantId());
                return parent;
            }

        }
        return null;
    }

    @Override
    public TenantDto getTenant(Long id) {
        SysTenant sysTenantVo = tenantMapper.selectById(id);

        return BeanUtil.toBean(sysTenantVo, TenantDto.class);
    }

    @Override
    public List<TenantDto> getTenants(List<Long> id) {
        List<SysTenant> sysTenants = tenantMapper.selectBatchIds(id);

        return BeanUtil.copyToList(sysTenants, TenantDto.class);
    }

    /**
     * 根据商户号获取商户信息
     *
     * @param mchNo
     * @return
     */
    @Override
    public TenantDto getTenantByNo(String mchNo) {
        SysTenant sysTenantVo = tenantMapper.selectOne(Wrappers.<SysTenant>lambdaQuery().eq(SysTenant::getCode, mchNo));
        return BeanUtil.toBean(sysTenantVo, TenantDto.class);
    }

    /**
     * 添加商户余额
     *
     * @param tenantId
     * @param realAmount
     */
    @Override
    public void balanceIncrease(Long tenantId, BigDecimal realAmount) {
        tenantService.balanceIncrease(tenantId, realAmount);
    }

    /**
     * 减少余额
     *
     * @param tenantId
     * @param realAmount
     */
    @Override
    public void balanceReduce(Long tenantId, BigDecimal realAmount) {
        tenantService.balanceReduce(tenantId, realAmount);
    }

    /**
     * 冻结金额
     *
     * @param tenantId
     * @param realAmount
     */
    @Override
    public void balanceFreeze(Long tenantId, BigDecimal realAmount) {
        tenantService.balanceFreeze(tenantId, realAmount);
    }

    @Override
    public TenantNotifyDto findNotifyByOrderId(Long id) {
        return tenantNotifyMapper.selectVoOne(Wrappers.<TenantNotify>lambdaQuery().eq(TenantNotify::getOrderId, id), TenantNotifyDto.class);
    }

    @Override
    public void saveNotify(TenantNotifyDto mchNotifyRecord) {
        TenantNotify bean = BeanUtil.toBean(mchNotifyRecord, TenantNotify.class);
        tenantNotifyMapper.insert(bean);
        mchNotifyRecord.setNotifyId(bean.getNotifyId());
    }

    @Override
    public TenantNotifyDto getNotifyById(Long notifyId) {
        return tenantNotifyMapper.selectVoById(notifyId, TenantNotifyDto.class);
    }

    @Override
    public void updateNotifyResult(Long notifyId, int stateSuccess, String res) {
        tenantNotifyMapper.update(Wrappers.<TenantNotify>update().eq("notify_id", notifyId)
                .set("state", stateSuccess)
                .set("res_result", res)
        );
    }

    @Override
    public List<TenantDto> getAllTenant() {
        return tenantMapper.selectVoList(Wrappers.emptyWrapper(), TenantDto.class);
    }

    /**
     * 获取直属下级
     *
     * @param tenantId
     * @return
     */
    @Override
    public List<Long> getChildTenant(Long tenantId) {

        List<SysTenant> sysTenants = tenantMapper.selectChildList(tenantId);
        if (ObjectUtil.isNotEmpty(sysTenants)) {
            return sysTenants.stream().map(SysTenant::getId).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    @Override
    public List<Long> getChildTenantAndSelf(Long tenantId) {

        List<Long> result = new ArrayList<>();
        result.add(tenantId);
        List<SysTenant> sysTenants = tenantMapper.selectChildList(tenantId);
        if (ObjectUtil.isNotEmpty(sysTenants)) {
            result.addAll(sysTenants.stream().map(SysTenant::getId).collect(Collectors.toList()));

        }

        return result;
    }

    /**
     * 根据chatID获取商户信息
     *
     * @param chatId
     * @return
     */
    @Override
    public TenantDto getTenantByChatId(Long chatId) {
        SysTenant tenant = tenantMapper.selectOne(Wrappers.<SysTenant>query().eq("telegram", chatId));
        if (ObjectUtil.isNotEmpty(tenant)) {
            return BeanUtil.toBean(tenant, TenantDto.class);
        }
        return null;
    }

    /**
     * 冻结金额释放,余额减少
     *
     * @param tenantId
     * @param amount
     */
    @Override
    public void balanceRelease(Long tenantId, BigDecimal amount) {
        tenantService.balanceRelease(tenantId, amount);
    }

    @Override
    public TenantDto getTenantByDomain(String host) {
        return tenantService.getByDomain(host);
    }
}
