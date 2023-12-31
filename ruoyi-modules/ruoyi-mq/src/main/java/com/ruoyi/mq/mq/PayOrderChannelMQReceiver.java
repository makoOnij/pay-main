/*
 * Copyright (c) 2021-2031, 河北计全科技有限公司 (https://www.jeequan.com & jeequan@126.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ruoyi.mq.mq;

import com.ruoyi.boss.api.RemoteBossService;
import com.ruoyi.common.core.dto.PayOrderChannelDto;
import com.ruoyi.common.core.dto.PayOrderDto;
import com.ruoyi.common.core.enums.OrderStatus;
import com.ruoyi.mq.IMQSender;
import com.ruoyi.mq.api.model.PayOrderChannelMQ;
import com.ruoyi.order.api.RemoteOrderService;
import com.ruoyi.payment.api.RemotePaymentService;
import com.ruoyi.system.api.RemoteTenantService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 接收MQ消息
 * 业务： 订单上游代付出款
 */
@Slf4j
@Component
public class PayOrderChannelMQReceiver implements PayOrderChannelMQ.IMQReceiver {

    @DubboReference
    private RemoteOrderService payOrderService;
    @DubboReference
    private RemoteTenantService mchNotifyRecordService;

    @DubboReference
    private RemoteBossService remoteBossService;

    @DubboReference
    private RemotePaymentService paymentService;
    @Autowired
    private IMQSender mqSender;

    @Override
    public void receive(PayOrderChannelMQ.MsgPayload payload) {

        try {
            log.info("接收上游通道支付MQ, msg={}", payload.toString());

            Long notifyId = payload.getOrderId();
            PayOrderDto orderDto = payOrderService.getById(notifyId);
            if (orderDto == null) {
                log.info("订单信息错误");
                return;
            }
            if (orderDto.getStatus().equals(OrderStatus.SUCCESS.getCode())) {
                log.info("订单:{} 已代付成功", orderDto.getId());
                return;
            }
            PayOrderChannelDto channelByCode = payOrderService.getChannelByCode(orderDto.getWayCode(), orderDto.getId());
            if (channelByCode.getRetryCount() > 3) {
                log.info("已达到最大发送次数");
                payOrderService.changeChannel(orderDto.getId());
                //重新通知调用其他通道
                mqSender.send(PayOrderChannelMQ.build(notifyId), 1);
                return;
            }

            paymentService.processPay(orderDto);
            // 通知延时次数
            mqSender.send(PayOrderChannelMQ.build(notifyId), channelByCode.getRetryCount() * 5);
        } catch (Exception e) {
            log.error("调用上游通道发生异常:{}", e.getMessage());
        }
    }
}
