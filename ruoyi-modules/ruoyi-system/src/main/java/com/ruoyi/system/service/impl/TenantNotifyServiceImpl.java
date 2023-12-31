package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.dto.TenantNotifyDto;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.system.domain.TenantNotify;
import com.ruoyi.system.mapper.TenantNotifyMapper;
import com.ruoyi.system.service.ITenantNotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 商户通知Service业务层处理
 *
 * @author ruoyi
 * @date 2023-11-27
 */
@RequiredArgsConstructor
@Service
public class TenantNotifyServiceImpl implements ITenantNotifyService {

    private final TenantNotifyMapper baseMapper;

    /**
     * 查询商户通知
     */
    @Override
    public TenantNotifyDto queryById(Long notifyId) {
        return baseMapper.selectVoById(notifyId);
    }

    /**
     * 查询商户通知列表
     */
    @Override
    public TableDataInfo<TenantNotifyDto> queryPageList(TenantNotifyDto bo, PageQuery pageQuery) {
        LambdaQueryWrapper<TenantNotify> lqw = buildQueryWrapper(bo);
        Page<TenantNotifyDto> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询商户通知列表
     */
    @Override
    public List<TenantNotifyDto> queryList(TenantNotifyDto bo) {
        LambdaQueryWrapper<TenantNotify> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<TenantNotify> buildQueryWrapper(TenantNotifyDto bo) {
        LambdaQueryWrapper<TenantNotify> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getOrderId() != null, TenantNotify::getOrderId, bo.getOrderId());
        lqw.eq(bo.getOrderType() != null, TenantNotify::getOrderType, bo.getOrderType());
        lqw.eq(StringUtils.isNotBlank(bo.getMchOrderId()), TenantNotify::getMchOrderId, bo.getMchOrderId());
        lqw.eq(StringUtils.isNotBlank(bo.getTenantName()), TenantNotify::getTenantName, bo.getTenantName());
        lqw.eq(bo.getTenantId() != null, TenantNotify::getTenantId, bo.getTenantId());
        lqw.eq(StringUtils.isNotBlank(bo.getNotifyUrl()), TenantNotify::getNotifyUrl, bo.getNotifyUrl());
        lqw.eq(StringUtils.isNotBlank(bo.getResResult()), TenantNotify::getResResult, bo.getResResult());
        lqw.eq(bo.getNotifyCount() != null, TenantNotify::getNotifyCount, bo.getNotifyCount());
        lqw.eq(bo.getState() != null, TenantNotify::getState, bo.getState());
        lqw.eq(bo.getLastNotifyTime() != null, TenantNotify::getLastNotifyTime, bo.getLastNotifyTime());
        return lqw;
    }

    /**
     * 新增商户通知
     */
    @Override
    public Boolean insertByBo(TenantNotifyDto bo) {
        TenantNotify add = BeanUtil.toBean(bo, TenantNotify.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setNotifyId(add.getNotifyId());
        }
        return flag;
    }

    /**
     * 修改商户通知
     */
    @Override
    public Boolean updateByBo(TenantNotifyDto bo) {
        TenantNotify update = BeanUtil.toBean(bo, TenantNotify.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(TenantNotify entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除商户通知
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
