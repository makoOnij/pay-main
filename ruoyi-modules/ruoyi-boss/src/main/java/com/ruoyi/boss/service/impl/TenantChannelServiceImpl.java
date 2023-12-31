package com.ruoyi.boss.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.boss.api.domain.TenantWayDto;
import com.ruoyi.boss.domain.TenantChannel;
import com.ruoyi.boss.domain.bo.TenantChannelBo;
import com.ruoyi.boss.domain.vo.PayChannelVo;
import com.ruoyi.boss.domain.vo.TenantChannelVo;
import com.ruoyi.boss.mapper.TenantChannelMapper;
import com.ruoyi.boss.service.IPayChannelService;
import com.ruoyi.boss.service.ITenantChannelService;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.common.core.enums.Currency;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.system.api.RemoteTenantService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 代付通道Service业务层处理
 *
 * @author ruoyi
 * @date 2023-11-24
 */
@RequiredArgsConstructor
@Service
public class TenantChannelServiceImpl implements ITenantChannelService {

    private final TenantChannelMapper baseMapper;

    private final IPayChannelService payWayService;


    @DubboReference
    private RemoteTenantService remoteTenantService;

    /**
     * 查询代付通道
     */
    @Override
    public TenantChannelVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询代付通道列表
     */
    @Override
    public TableDataInfo<TenantChannelVo> queryPageList(TenantChannelBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<TenantChannel> lqw = buildQueryWrapper(bo);
        Page<TenantChannelVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询代付通道列表
     */
    @Override
    public List<TenantChannelVo> queryList(TenantChannelBo bo) {
        LambdaQueryWrapper<TenantChannel> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw, TenantChannelVo.class);
    }

    private LambdaQueryWrapper<TenantChannel> buildQueryWrapper(TenantChannelBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<TenantChannel> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getPayId() != null, TenantChannel::getPayId, bo.getPayId());
        lqw.like(StringUtils.isNotBlank(bo.getPayName()), TenantChannel::getPayName, bo.getPayName());
        lqw.eq(StringUtils.isNotBlank(bo.getPayCode()), TenantChannel::getPayCode, bo.getPayCode());
        lqw.eq(bo.getRate() != null, TenantChannel::getRate, bo.getRate());
        lqw.eq(bo.getFee() != null, TenantChannel::getFee, bo.getFee());
        lqw.like(StringUtils.isNotBlank(bo.getName()), TenantChannel::getName, bo.getName());
        lqw.eq(bo.getSort() != null, TenantChannel::getSort, bo.getSort());
        lqw.eq(StringUtils.isNotBlank(bo.getCurrency()), TenantChannel::getCurrency, bo.getCurrency());
        lqw.eq(ObjectUtil.isNotEmpty(bo.getWayType()), TenantChannel::getWayType, bo.getWayType());
        lqw.eq(ObjectUtil.isNotEmpty(bo.getStatus()), TenantChannel::getStatus, bo.getStatus());
        lqw.eq(bo.getMaxAmount() != null, TenantChannel::getMaxAmount, bo.getMaxAmount());
        lqw.eq(bo.getMinAmount() != null, TenantChannel::getMinAmount, bo.getMinAmount());
        lqw.eq(bo.getFixedAmount() != null, TenantChannel::getFixedAmount, bo.getFixedAmount());
        lqw.eq(bo.getTenantId() != null, TenantChannel::getTenantId, bo.getTenantId());
        return lqw;
    }

    /**
     * 新增代付通道
     */
    @Override
    public Boolean insertByBo(TenantChannelBo bo) {

        boolean exists = baseMapper.exists(Wrappers.<TenantChannel>lambdaQuery().eq(TenantChannel::getTenantId, bo.getTenantId())
                .eq(TenantChannel::getPayId, bo.getPayId())
        );
        if (exists) {
            throw new ServiceException("此商户已经配置了该通道");
        }

        TenantDto tenant = remoteTenantService.getTenant(bo.getTenantId());
        if (tenant == null) {
            throw new ServiceException("商户信息错误");
        }
        PayChannelVo payChannelVo = payWayService.queryById(bo.getPayId());
        if (payChannelVo == null) {
            throw new ServiceException("通道信息错误");
        }
        bo.setTenantName(tenant.getName());
        bo.setPayCode(payChannelVo.getCode());
        bo.setPayName(payChannelVo.getName());
        bo.setRate(tenant.getChargeRate());
        bo.setFee(tenant.getPayforFee());
        bo.setWayType(0);
        TenantChannel add = BeanUtil.toBean(bo, TenantChannel.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改代付通道
     */
    @Override
    public Boolean updateByBo(TenantChannelBo bo) {
        TenantChannel update = BeanUtil.toBean(bo, TenantChannel.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(TenantChannel entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除代付通道
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public int updateChannel(List<Long> ids, TenantChannel merchantWay) {


        List<TenantDto> tenants = remoteTenantService.getTenants(ids);
        //查询通道信息
        TenantChannel tenantChannel = baseMapper.selectById(merchantWay.getId());
        List<TenantChannel> collect = tenants.stream().map(p -> {
            TenantChannel tenantChannelDto = new TenantChannel();
            tenantChannelDto.setTenantId(p.getId());
            tenantChannelDto.setTenantName(p.getName());
            tenantChannelDto.setStatus(merchantWay.getStatus());
            tenantChannelDto.setCurrency(Currency.CNY.getCurrencyType());
            tenantChannelDto.setFee(BigDecimal.ZERO);
            tenantChannelDto.setName(p.getName() + ":通道");
            tenantChannelDto.setWayType(0);

            tenantChannelDto.setMaxAmount(tenantChannel.getMaxAmount());
            tenantChannelDto.setMinAmount(tenantChannel.getMinAmount());
            tenantChannelDto.setPayCode(tenantChannel.getPayCode());
            tenantChannelDto.setPayName(tenantChannel.getPayName());
            tenantChannelDto.setRate(tenantChannel.getRate());
            tenantChannelDto.setPayId(tenantChannel.getPayId());
            tenantChannelDto.setFixedAmount(BigDecimal.ZERO);

            return tenantChannelDto;
        }).collect(Collectors.toList());

        baseMapper.duplicateKeyUpdate(collect);

        return 1;
    }

    /**
     * 修改状态
     *
     * @param id
     * @param status
     * @return
     */
    @Override
    public int updateStatus(Long id, Integer status) {
        return baseMapper.update(Wrappers.<TenantChannel>update().set("status", status).eq("id", id));
    }

    @Override
    public TenantWayDto queryBestWay(Long id, List<String> exclude) {
        TenantWayDto tenantWayVo = baseMapper.selectVoOne(Wrappers.<TenantChannel>lambdaQuery()
                        .eq(TenantChannel::getTenantId, id)
                        .notIn(ObjectUtil.isNotEmpty(exclude), TenantChannel::getPayCode, exclude)
                        .orderByDesc(TenantChannel::getSort)
                        .last("limit 1")

                , TenantWayDto.class);
        return tenantWayVo;
    }
}
