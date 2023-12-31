package com.ruoyi.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.boss.api.RemoteBossService;
import com.ruoyi.boss.api.domain.TenantWayDto;
import com.ruoyi.common.core.dto.PayOrderChannelDto;
import com.ruoyi.common.core.dto.PayOrderDto;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.common.core.enums.ChannelStatusEnum;
import com.ruoyi.common.core.enums.OrderStatus;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.mq.api.RemoteMQService;
import com.ruoyi.mq.api.model.PayOrderChannelMQ;
import com.ruoyi.order.api.dto.OrderPayforDto;
import com.ruoyi.order.domain.OrderPay;
import com.ruoyi.order.domain.OrderPayChannel;
import com.ruoyi.order.mapper.PayOrderChannelMapper;
import com.ruoyi.order.mapper.PayOrderMapper;
import com.ruoyi.order.service.IOrderPayforService;
import com.ruoyi.payment.api.RemotePaymentService;
import com.ruoyi.system.api.RemoteTenantService;
import com.ruoyi.system.api.model.LoginUser;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 代付订单Service业务层处理
 *
 * @author ruoyi
 * @date 2023-11-22
 */
@RequiredArgsConstructor
@Service
public class OrderPayforServiceImpl implements IOrderPayforService {

    private final PayOrderMapper baseMapper;
    @DubboReference
    private RemotePaymentService paymentService;
    @DubboReference
    private RemoteTenantService remoteTenantService;

    @DubboReference
    private RemoteBossService remoteBossService;

    private final PayOrderChannelMapper payOrderChannelMapper;
    @DubboReference
    private RemoteMQService remoteMQService;
    /**
     * 查询代付订单
     */
    @Override
    public OrderPayforDto queryById(Long id) {
        OrderPayforDto orderPayforDto = baseMapper.selectVoById(id, OrderPayforDto.class);
        if (ObjectUtil.isNotEmpty(orderPayforDto)) {
            List<PayOrderChannelDto> payOrderChannelDtos = payOrderChannelMapper.selectVoList(Wrappers.<OrderPayChannel>lambdaQuery()
                    .eq(OrderPayChannel::getOrderNo, orderPayforDto.getOrderNo()), PayOrderChannelDto.class);
            orderPayforDto.setChannelList(payOrderChannelDtos);
        }
        return orderPayforDto;
    }

    /**
     * 查询代付订单列表
     */
    @Override
    public TableDataInfo<OrderPayforDto> queryPageList(OrderPayforDto bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OrderPay> lqw = buildQueryWrapper(bo);
        Page<OrderPayforDto> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询代付订单列表
     */
    @Override
    public List<OrderPayforDto> queryList(OrderPayforDto bo) {
        LambdaQueryWrapper<OrderPay> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<OrderPay> buildQueryWrapper(OrderPayforDto bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<OrderPay> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getTenantId() != null, OrderPay::getTenantId, bo.getTenantId());
        lqw.like(StringUtils.isNotBlank(bo.getTenantName()), OrderPay::getTenantName, bo.getTenantName());
        lqw.eq(bo.getOrderNo() != null, OrderPay::getOrderNo, bo.getOrderNo());
        lqw.eq(bo.getAppOrderNo() != null, OrderPay::getAppOrderNo, bo.getAppOrderNo());
        lqw.eq(bo.getPayforFee() != null, OrderPay::getPayforFee, bo.getPayforFee());
        lqw.like(StringUtils.isNotBlank(bo.getUserName()), OrderPay::getUserName, bo.getUserName());
        lqw.eq(StringUtils.isNotBlank(bo.getBankAccount()), OrderPay::getBankAccount, bo.getBankAccount());
        lqw.like(StringUtils.isNotBlank(bo.getBankName()), OrderPay::getBankName, bo.getBankName());
        lqw.eq(bo.getStatus() != null, OrderPay::getStatus, bo.getStatus());
        lqw.eq(bo.getAmount() != null, OrderPay::getAmount, bo.getAmount());
        lqw.eq(bo.getBackTime() != null, OrderPay::getBackTime, bo.getBackTime());
        lqw.in(bo.getTenantIds() != null, OrderPay::getTenantId, bo.getTenantIds());
        lqw.orderByDesc(OrderPay::getCreateTime);
        return lqw;
    }

    /**
     * 新增代付订单
     */
    @Override
    public Boolean insertByBo(OrderPayforDto bo) {
        OrderPay add = BeanUtil.toBean(bo, OrderPay.class);
        LoginUser loginUser = LoginHelper.getLoginUser();
        add.setTenantId(loginUser.getTenantId());
        add.setTenantName(loginUser.getTenantName());
        TenantDto channel = remoteTenantService.getChannel(loginUser.getTenantId());

        add.setParentName(channel.getTenantName());
        add.setParentId(channel.getId());
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改代付订单
     */
    @Override
    public Boolean updateByBo(OrderPayforDto bo) {
        OrderPay update = BeanUtil.toBean(bo, OrderPay.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(OrderPay entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除代付订单
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 订单审核
     *
     * @param bo
     * @return
     */
    @Override
    public int audit(OrderPayforDto bo) {
        PayOrderDto payOrderDto = baseMapper.selectVoById(bo.getId(), PayOrderDto.class);
        if (ObjectUtil.isEmpty(payOrderDto)) {
            throw new ServiceException("订单信息不存在");
        }
        int update = baseMapper.update(Wrappers.<OrderPay>update().eq("id", bo.getId())
                .set("status", bo.getStatus())
                .set("remark", bo.getRemark())
        );
        if (update > 0) {
            //审核成功调取支付通道
            if (bo.getStatus().equals(OrderStatus.AUDITSUCCESS.getCode())) {
                remoteMQService.send(PayOrderChannelMQ.build(payOrderDto.getId()));
            }
        }

        return update;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateIng2Success(String orderNo) {

        baseMapper.update(Wrappers.<OrderPay>update().eq("order_no", orderNo)
                .set("status", OrderStatus.SUCCESS.getCode()));

        payOrderChannelMapper.update(Wrappers.<OrderPayChannel>update().eq("order_no", orderNo)
                .set("status", OrderStatus.SUCCESS.getCode()));

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateIng2Fail(String orderNo, String channelErrCode, String channelErrMsg) {
        baseMapper.update(Wrappers.<OrderPay>update().eq("order_no", orderNo)
                .set("status", OrderStatus.FAIL.getCode())
                .set("error_message", channelErrMsg)
                .set("error_code", channelErrCode)
        );
        payOrderChannelMapper.update(Wrappers.<OrderPayChannel>update().eq("order_no", orderNo)
                .set("status", OrderStatus.FAIL.getCode())
                .set("error_message", channelErrMsg)
                .set("error_code", channelErrCode)
        );

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addOrder(PayOrderDto model) {

        OrderPay order = BeanUtil.toBean(model, OrderPay.class);
        baseMapper.insert(order);
        //添加通道处理信息
        OrderPayChannel orderPayChannel = new OrderPayChannel();
        orderPayChannel.setOrderId(order.getId());
        orderPayChannel.setOrderNo(order.getOrderNo());
        orderPayChannel.setWayId(model.getWayId());
        orderPayChannel.setWayCode(model.getWayCode());
        orderPayChannel.setRetryCount(0);
        orderPayChannel.setStatus(ChannelStatusEnum.WAIT.getCode());
        payOrderChannelMapper.insert(orderPayChannel);

        return order.getId();

    }

    /**
     * 调起通道出款
     *
     * @param id
     * @return
     */
    @Override
    public int channel(Long id) {
        OrderPay orderPay = baseMapper.selectById(id);
        if (ObjectUtil.isEmpty(orderPay)) {
            throw new ServiceException("订单不存在");
        }
        if (orderPay.getStatus().equals(OrderStatus.SUCCESS.getCode())) {
            throw new ServiceException("订单上游状态已成功");
        }
        remoteMQService.send(PayOrderChannelMQ.build(id));
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeChannel(Long id) {
        OrderPay orderPay = baseMapper.selectById(id);
        if (ObjectUtil.isNotEmpty(orderPay)) {
            //查询当前订单使用的通道
            List<OrderPayChannel> orderPayChannels = payOrderChannelMapper.selectList(Wrappers.<OrderPayChannel>lambdaQuery()
                    .eq(OrderPayChannel::getOrderNo, orderPay.getOrderNo())
            );
            List<String> codes = orderPayChannels.stream().map(OrderPayChannel::getWayCode).collect(Collectors.toList());
            TenantWayDto nextWay = remoteBossService.getBestWay(orderPay.getTenantId(), codes);
            baseMapper.update(null, Wrappers.<OrderPay>lambdaUpdate()
                    .eq(OrderPay::getId, id)
                    .set(OrderPay::getWayCode, nextWay.getPayCode())
                    .set(OrderPay::getWayName, nextWay.getPayName())
                    .set(OrderPay::getWayFee, nextWay.getFee())
                    .set(OrderPay::getWayRate, nextWay.getRate())
            );
            OrderPayChannel channel = new OrderPayChannel();
            channel.setOrderId(id);
            channel.setOrderNo(orderPay.getOrderNo());
            channel.setStatus(ChannelStatusEnum.WAIT.getCode());
            channel.setWayCode(nextWay.getPayCode());
            channel.setWayId(nextWay.getId());
            channel.setRetryCount(0);
            payOrderChannelMapper.insert(channel);
        }
    }

}
