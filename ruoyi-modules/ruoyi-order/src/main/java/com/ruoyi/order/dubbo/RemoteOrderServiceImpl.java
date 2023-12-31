package com.ruoyi.order.dubbo;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.dto.PayOrderChannelDto;
import com.ruoyi.common.core.dto.PayOrderDto;
import com.ruoyi.common.core.enums.ChannelStatusEnum;
import com.ruoyi.common.core.enums.OrderStatus;
import com.ruoyi.order.api.RemoteOrderService;
import com.ruoyi.order.api.dto.OrderChargeDto;
import com.ruoyi.order.domain.OrderCharge;
import com.ruoyi.order.domain.OrderPay;
import com.ruoyi.order.domain.OrderPayChannel;
import com.ruoyi.order.mapper.OrderChargeMapper;
import com.ruoyi.order.mapper.PayOrderChannelMapper;
import com.ruoyi.order.mapper.PayOrderMapper;
import com.ruoyi.order.service.IOrderPayforService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteOrderServiceImpl implements RemoteOrderService {


    private  final PayOrderMapper orderMapper;
    private final OrderChargeMapper orderChargeMapper;

    private final IOrderPayforService orderPayforService;

    private final PayOrderChannelMapper payOrderChannelMapper;


    @Override
    public Boolean exitsOrder(String mchNo, String appOrderNo) {
        Long l = orderMapper.selectCount(Wrappers.<OrderPay>lambdaQuery().eq(OrderPay::getAppOrderNo, appOrderNo).eq(OrderPay::getTenantCode, mchNo));
        return l>0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateInit2Ing(String orderNo, PayOrderDto payOrderDto) {
        orderMapper.update(Wrappers.<OrderPay>update().eq("order_no", orderNo)
            .set("status", payOrderDto.getStatus())
            .set("error_message", payOrderDto.getErrorMessage())
            .set("error_code", payOrderDto.getErrorCode())
        );
        payOrderChannelMapper.update(Wrappers.<OrderPayChannel>update()
                .eq("order_no", orderNo)
                .eq("way_code", payOrderDto.getWayCode())
                .set("status", payOrderDto.getStatus())
                .set("error_message", payOrderDto.getErrorMessage())
                .set("error_code", payOrderDto.getErrorCode())
                .setSql(payOrderDto.getStatus().equals(OrderStatus.FAIL.getCode()), "retry_count=retry_count+1")
        );
        return true;
    }

    @Override
    public boolean updateIng2SuccessOrFail(Long id, Byte status, String channelOrderId, String channelUserId, String channelErrCode, String channelErrMsg) {
        return false;
    }

    @Override
    public PayOrderDto getByOrderNo(String orderNo) {
        OrderPay orderPay = orderMapper.selectOne(Wrappers.<OrderPay>lambdaQuery().eq(OrderPay::getOrderNo, orderNo));
        return BeanUtil.toBean(orderPay, PayOrderDto.class);
    }

    @Override
    public PayOrderDto getById(Long orderId) {
        return orderMapper.selectVoById(orderId, PayOrderDto.class);
    }

    /**
     * 查询上游支付通道信息
     *
     * @param code
     * @return
     */
    @Override
    public PayOrderChannelDto getChannelByCode(String code, Long orderId) {
        return payOrderChannelMapper.selectVoOne(Wrappers.<OrderPayChannel>lambdaQuery()
                        .eq(OrderPayChannel::getWayCode, code)
                        .eq(OrderPayChannel::getOrderId, orderId)
                        .eq(OrderPayChannel::getStatus, ChannelStatusEnum.WAIT.getCode())
                        .orderByDesc(OrderPayChannel::getCreateTime)
                        .last(" limit 1")
                , PayOrderChannelDto.class);
    }

    @Override
    public boolean updateIng2Success(String orderNo) {
        return orderPayforService.updateIng2Success(orderNo);
    }

    @Override
    public boolean updateIng2Fail(String orderNo, String channelErrCode, String channelErrMsg) {
        return orderPayforService.updateIng2Fail(orderNo, channelErrCode, channelErrMsg);
    }

    @Override
    public void confirmSuccess(PayOrderDto payOrderDto) {

    }

    @Override
    public PayOrderDto queryMchOrder(String mchNo, String payOrderId, String mchOrderNo) {
        return null;
    }

    @Override
    public void updateInit2Close(Long id) {

    }

    @Override
    public void updateIng2Close(Long payOrderId) {

    }

    /**
     * 创建充值订单
     *
     */
    @Override
    public void addOrder(OrderChargeDto orderChargeDto) {
        orderChargeMapper.insert(BeanUtil.toBean(orderChargeDto, OrderCharge.class));
    }

    /**
     * 创建订单
     *
     * @param model
     */
    @Override
    public Long addOrder(PayOrderDto model) {
        return orderPayforService.addOrder(model);
    }

    @Override
    public void updateNotifySent(Long orderId) {
        OrderPay orderPay = orderMapper.selectById(orderId);
        OrderPay orderPay1 = new OrderPay();
        orderPay1.setStatus(OrderStatus.NOTIFYING.getCode());
        orderPay1.setId(orderPay.getId());
        orderMapper.updateById(orderPay1);
    }

    @Override
    public void changeChannel(Long id) {
        orderPayforService.changeChannel(id);
    }
}
