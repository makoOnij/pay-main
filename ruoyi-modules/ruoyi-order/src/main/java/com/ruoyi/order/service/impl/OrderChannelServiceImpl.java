package com.ruoyi.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.order.api.dto.OrderChannelBo;
import com.ruoyi.order.domain.OrderChannel;
import com.ruoyi.order.domain.vo.OrderChannelVo;
import com.ruoyi.order.mapper.OrderChannelMapper;
import com.ruoyi.order.service.IOrderChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 渠道充值订单Service业务层处理
 *
 * @author ruoyi
 * @date 2023-11-22
 */
@RequiredArgsConstructor
@Service
public class OrderChannelServiceImpl implements IOrderChannelService {

    private final OrderChannelMapper baseMapper;

    /**
     * 查询渠道充值订单
     */
    @Override
    public OrderChannelVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询渠道充值订单列表
     */
    @Override
    public TableDataInfo<OrderChannelVo> queryPageList(OrderChannelBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OrderChannel> lqw = buildQueryWrapper(bo);
        Page<OrderChannelVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询渠道充值订单列表
     */
    @Override
    public List<OrderChannelVo> queryList(OrderChannelBo bo) {
        LambdaQueryWrapper<OrderChannel> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<OrderChannel> buildQueryWrapper(OrderChannelBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<OrderChannel> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getChannelName()), OrderChannel::getChannelName, bo.getChannelName());
        lqw.eq(bo.getStatus() != null, OrderChannel::getStatus, bo.getStatus());
        lqw.eq(bo.getAmount() != null, OrderChannel::getAmount, bo.getAmount());
        lqw.eq(bo.getRate() != null, OrderChannel::getRate, bo.getRate());
        lqw.eq(bo.getSettleRate() != null, OrderChannel::getSettleRate, bo.getSettleRate());
        lqw.eq(bo.getRealAmount() != null, OrderChannel::getRealAmount, bo.getRealAmount());
        return lqw;
    }

    /**
     * 新增渠道充值订单
     */
    @Override
    public Boolean insertByBo(OrderChannelBo bo) {
        OrderChannel add = BeanUtil.toBean(bo, OrderChannel.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改渠道充值订单
     */
    @Override
    public Boolean updateByBo(OrderChannelBo bo) {
        OrderChannel update = BeanUtil.toBean(bo, OrderChannel.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(OrderChannel entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除渠道充值订单
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
