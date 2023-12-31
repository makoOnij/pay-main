package com.ruoyi.boss.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.boss.domain.PayChannel;
import com.ruoyi.boss.domain.TenantChannel;
import com.ruoyi.boss.domain.bo.PayChannelBo;
import com.ruoyi.boss.domain.vo.PayChannelVo;
import com.ruoyi.boss.mapper.PayChannelMapper;
import com.ruoyi.boss.mapper.TenantChannelMapper;
import com.ruoyi.boss.service.IPayChannelService;
import com.ruoyi.common.core.enums.OpenStatus;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 通道列表Service业务层处理
 *
 * @author ruoyi
 * @date 2023-11-24
 */
@RequiredArgsConstructor
@Service
public class PayChannelServiceImpl implements IPayChannelService {

    private final PayChannelMapper baseMapper;

    private final TenantChannelMapper tenantChannelMapper;

    /**
     * 查询通道列表
     */
    @Override
    public PayChannelVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询通道列表列表
     */
    @Override
    public TableDataInfo<PayChannelVo> queryPageList(PayChannelBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PayChannel> lqw = buildQueryWrapper(bo);
        Page<PayChannelVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询通道列表列表
     */
    @Override
    public List<PayChannelVo> queryList(PayChannelBo bo) {
        LambdaQueryWrapper<PayChannel> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<PayChannel> buildQueryWrapper(PayChannelBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<PayChannel> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), PayChannel::getName, bo.getName());
        lqw.eq(bo.getMaxAmount() != null, PayChannel::getMaxAmount, bo.getMaxAmount());
        lqw.eq(bo.getMinAmount() != null, PayChannel::getMinAmount, bo.getMinAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getCode()), PayChannel::getCode, bo.getCode());
        lqw.eq(ObjectUtil.isNotEmpty(bo.getStatus()), PayChannel::getStatus, bo.getStatus());
        lqw.eq(bo.getRate() != null, PayChannel::getRate, bo.getRate());
        lqw.eq(bo.getRefund() != null, PayChannel::getRefund, bo.getRefund());
        lqw.eq(StringUtils.isNotBlank(bo.getRemark()), PayChannel::getRemark, bo.getRemark());
        return lqw;
    }

    /**
     * 新增通道列表
     */
    @Override
    public Boolean insertByBo(PayChannelBo bo) {
        PayChannel add = BeanUtil.toBean(bo, PayChannel.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改通道列表
     */
    @Override
    public Boolean updateByBo(PayChannelBo bo) {
        PayChannel update = BeanUtil.toBean(bo, PayChannel.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(PayChannel entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除通道列表
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public Boolean updateStatusByIds(Collection<Long> ids, Integer status) {
        return baseMapper.update(Wrappers.<PayChannel>update().in("id", ids).set("status", status)) > 0;
    }

    /**
     * 更新状态
     *
     * @param id
     * @param status
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(Long id, Integer status) {
        baseMapper.update(Wrappers.<PayChannel>update().eq("id", id).set("status", status));
        //更新商户通道配置
        tenantChannelMapper.update(Wrappers.<TenantChannel>update().eq("pay_id", id).set("status", status));
        return 1;
    }

    /**
     * 查询可用通道
     *
     * @return
     */
    @Override
    public List<PayChannelVo> queryValidList() {
        LambdaQueryWrapper<PayChannel> lqw = Wrappers.<PayChannel>lambdaQuery().eq(PayChannel::getStatus, OpenStatus.OPEN.getCode());
        return baseMapper.selectVoList(lqw);
    }
}
