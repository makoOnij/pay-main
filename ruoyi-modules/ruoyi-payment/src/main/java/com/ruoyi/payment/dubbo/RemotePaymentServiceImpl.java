package com.ruoyi.payment.dubbo;

import com.ruoyi.common.core.domain.ChannelRetMsg;
import com.ruoyi.common.core.dto.PayOrderDto;
import com.ruoyi.payment.api.RemotePaymentService;
import com.ruoyi.payment.service.PayOrderProcessService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

@DubboService
public class RemotePaymentServiceImpl implements RemotePaymentService {


    @Autowired
    private PayOrderProcessService payOrderProcessService;

    /**
     * 调用三方通道出款
     *
     * @param dto
     */
    @Override
    @Async
    public ChannelRetMsg.ChannelState processPay(PayOrderDto dto) {
        return payOrderProcessService.processPay(dto);
    }
}
