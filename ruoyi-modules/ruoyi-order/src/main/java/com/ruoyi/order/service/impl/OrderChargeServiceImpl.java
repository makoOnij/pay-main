package com.ruoyi.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.lock.LockTemplate;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.boss.api.RemoteBossService;
import com.ruoyi.boss.api.dto.DataFlowDto;
import com.ruoyi.boss.api.dto.DataReportDto;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.common.core.enums.AuditStatus;
import com.ruoyi.common.core.enums.YesOrNoEnum;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.tenant.tenant.TenantContextHolder;
import com.ruoyi.order.api.dto.OrderChargeDto;
import com.ruoyi.order.domain.OrderCharge;
import com.ruoyi.order.mapper.OrderChargeMapper;
import com.ruoyi.order.service.IOrderChargeService;
import com.ruoyi.system.api.RemoteConfigService;
import com.ruoyi.system.api.RemoteTenantService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 商户充值订单Service业务层处理
 *
 * @author ruoyi
 * @date 2023-11-22
 */
@RequiredArgsConstructor
@Service
public class OrderChargeServiceImpl implements IOrderChargeService {

    private final OrderChargeMapper baseMapper;
    @DubboReference
    private RemoteTenantService remoteTenantService;

    @DubboReference
    private RemoteBossService remoteBossService;

    @DubboReference
    private RemoteConfigService remoteConfigService;
    @Autowired
    private LockTemplate lockTemplate;
    /**
     * 查询商户充值订单
     */
    @Override
    public OrderChargeDto queryById(Long id) {
        return BeanUtil.toBean(baseMapper.selectById(id), OrderChargeDto.class);
    }

    /**
     * 查询商户充值订单列表
     */
    @Override
    public TableDataInfo<OrderCharge> queryPageList(OrderChargeDto bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OrderCharge> lqw = buildQueryWrapper(bo);
        Page<OrderCharge> result = baseMapper.selectPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询商户充值订单列表
     */
    @Override
    public List<OrderChargeDto> queryList(OrderChargeDto bo) {
        LambdaQueryWrapper<OrderCharge> lqw = buildQueryWrapper(bo);
        return BeanUtil.copyToList(baseMapper.selectList(lqw), OrderChargeDto.class);
    }

    private LambdaQueryWrapper<OrderCharge> buildQueryWrapper(OrderChargeDto bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<OrderCharge> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getOrderNo()), OrderCharge::getOrderNo, bo.getOrderNo());
        lqw.eq(bo.getStatus() != null, OrderCharge::getStatus, bo.getStatus());
        lqw.in(ObjectUtil.isNotEmpty(bo.getTenantIds()), OrderCharge::getTenantId, bo.getTenantIds());
        lqw.orderByDesc(OrderCharge::getCreateTime);
        return lqw;
    }

    /**
     * 新增商户充值订单
     */
    @Override
    public Boolean insertByBo(OrderChargeDto bo) {
        OrderCharge add = BeanUtil.toBean(bo, OrderCharge.class);


        TenantDto tenant = remoteTenantService.getTenant(TenantContextHolder.getTenantId());
        if (ObjectUtil.isNotEmpty(tenant.getChargeRate()) && tenant.getChargeRate().compareTo(BigDecimal.ZERO) > 0) {
            add.setIsFloat(YesOrNoEnum.NO.getCode());
        } else {
            add.setIsFloat(YesOrNoEnum.YES.getCode());
        }
        add.setTenantName(tenant.getName());
        add.setTenantId(tenant.getId());
        TenantDto channel = remoteTenantService.getChannel(tenant.getId());
        add.setParentName(channel.getName());
        add.setParentId(channel.getId());
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改商户充值订单
     */
    @Override
    public Boolean updateByBo(OrderChargeDto bo) {
        OrderCharge update = BeanUtil.toBean(bo, OrderCharge.class);

        return baseMapper.updateById(update) > 0;
    }


    /**
     * 批量删除商户充值订单
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 审核订单
     *
     * @param bo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(OrderChargeDto bo) {
        OrderCharge orderCharge = baseMapper.selectById(bo.getId());
        if (!orderCharge.getStatus().equals(AuditStatus.WAIT.getCode())) {
            throw new ServiceException("订单禁止操作");
        }
        //查询商户信息
        TenantDto tenant = remoteTenantService.getTenant(bo.getTenantId());
        if (ObjectUtil.isEmpty(tenant)) {
            throw new ServiceException("商户信息错误");
        }
        baseMapper.update(Wrappers.<OrderCharge>update().eq("id", bo.getId())
                .set("status", bo.getStatus())
                .set(bo.getRemark() != null, "remark", bo.getRemark())
        );
        if (bo.getStatus().equals(AuditStatus.SUCCESS.getCode())) {

            remoteTenantService.balanceIncrease(bo.getTenantId(), orderCharge.getRealAmount());
            remoteBossService.insertFlow(DataFlowDto.buildCharge(tenant, orderCharge.getRealAmount()));
            remoteBossService.inertReport(DataReportDto.buildCharge(tenant, orderCharge.getRealAmount(), BigDecimal.ZERO));

        }
    }
}
