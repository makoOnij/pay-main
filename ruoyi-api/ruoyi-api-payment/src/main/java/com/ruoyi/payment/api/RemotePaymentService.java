package com.ruoyi.payment.api;


import com.ruoyi.common.core.domain.ChannelRetMsg;
import com.ruoyi.common.core.dto.PayOrderDto;

/**
 * 订单网关服务
 *
 * @author Lion Li
 */
public interface RemotePaymentService {
    /**
     * 调用三方通道出款
     *
     * @param dto
     */
    ChannelRetMsg.ChannelState processPay(PayOrderDto dto);


}
