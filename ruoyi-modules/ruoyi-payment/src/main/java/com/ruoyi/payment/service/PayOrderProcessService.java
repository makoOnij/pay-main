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
package com.ruoyi.payment.service;

import cn.hutool.extra.spring.SpringUtil;
import com.ruoyi.boss.api.RemoteBossService;
import com.ruoyi.boss.api.domain.PayWayDto;
import com.ruoyi.boss.api.dto.DataFlowDto;
import com.ruoyi.boss.api.dto.DataReportDto;
import com.ruoyi.common.core.domain.ChannelRetMsg;
import com.ruoyi.common.core.dto.PayOrderDto;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.common.core.enums.OrderStatus;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.order.api.RemoteOrderService;
import com.ruoyi.payment.channel.IPaymentService;
import com.ruoyi.payment.params.UnifiedOrderRS;
import com.ruoyi.resource.api.RemoteMessageService;
import com.ruoyi.resource.api.domain.WebsocketMessage;
import com.ruoyi.system.api.RemoteTenantService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/***
 * 订单处理通用逻辑
 *
 * @author terrfly
 * @site https://www.jeequan.com
 * @date 2021/8/22 16:50
 */
@Service
@Slf4j
public class PayOrderProcessService {

    @DubboReference
    private RemoteOrderService remoteOrderService;

    @DubboReference
    private RemoteBossService remoteBossService;

    @DubboReference
    private RemoteTenantService remoteTenantService;

    @Autowired
    private PayMchNotifyService payMchNotifyService;

    @DubboReference
    protected RemoteMessageService remoteMessageService;

    /**
     * 明确成功的处理逻辑（除更新订单其他业务）
     **/
    @Async
    public void confirmSuccess(PayOrderDto payOrder, TenantDto tenant) {
        //设置订单状态
        payOrder.setStatus(OrderStatus.SUCCESS.getCode());

        //发送商户通知
        payMchNotifyService.payOrderNotify(payOrder);

        //释放冻结金额
        remoteTenantService.balanceRelease(tenant.getId(), payOrder.getValidAmount());

        remoteBossService.insertFlow(DataFlowDto.buildPayfor(tenant, payOrder));

        remoteBossService.inertReport(DataReportDto.buildPayfor(tenant, payOrder));

    }

    /**
     * 处理返回的渠道信息，并更新订单状态
     * payOrder将对部分信息进行 赋值操作。
     **/
    private void processChannelMsg(ChannelRetMsg channelRetMsg, PayOrderDto payOrderDto) {

        //对象为空 || 上游返回状态为空， 则无需操作
        if (channelRetMsg == null || channelRetMsg.getChannelState() == null) {
            return;
        }

        //明确成功
        if (ChannelRetMsg.ChannelState.CONFIRM_SUCCESS == channelRetMsg.getChannelState()) {
            this.updateInitOrderStateThrowException(OrderStatus.SUCCESS.getCode(), payOrderDto, channelRetMsg);
            //明确失败
        } else if (ChannelRetMsg.ChannelState.CONFIRM_FAIL == channelRetMsg.getChannelState()) {
            this.updateInitOrderStateThrowException(OrderStatus.FAIL.getCode(), payOrderDto, channelRetMsg);
            // 上游处理中 || 未知 || 上游接口返回异常  订单为支付中状态
        } else if (ChannelRetMsg.ChannelState.WAITING == channelRetMsg.getChannelState() ||
                ChannelRetMsg.ChannelState.UNKNOWN == channelRetMsg.getChannelState() ||
                ChannelRetMsg.ChannelState.API_RET_ERROR == channelRetMsg.getChannelState()
        ) {
            this.updateInitOrderStateThrowException(OrderStatus.FAIL.getCode(), payOrderDto, channelRetMsg);

            // 系统异常：  订单不再处理。  为： 生成状态
        } else if (ChannelRetMsg.ChannelState.SYS_ERROR == channelRetMsg.getChannelState()) {

        } else {

            throw new ServiceException("ChannelState 返回异常！");
        }


    }

    /**
     * 更新订单状态 --》 订单生成--》 其他状态  (向外抛出异常)
     **/
    private void updateInitOrderStateThrowException(Integer orderState, PayOrderDto payOrderDto, ChannelRetMsg channelRetMsg) {

        payOrderDto.setStatus(orderState);

        payOrderDto.setErrorCode(channelRetMsg.getChannelErrCode());
        payOrderDto.setErrorMessage(channelRetMsg.getChannelErrMsg());
        boolean isSuccess = remoteOrderService.updateInit2Ing(payOrderDto.getOrderNo(), payOrderDto);

        if (!isSuccess) {
            throw new ServiceException("更新订单异常!");
        }
    }

    public ChannelRetMsg.ChannelState processPay(PayOrderDto payOrder) {

        IPaymentService paymentService = SpringUtil.getBean(payOrder.getWayCode() + "PaymentService", IPaymentService.class);
        if (paymentService == null) {
            throw new ServiceException("未支持此通道,请联系管理员");
        }
        //获取支付参数 (缓存数据) 和 商户信息
        TenantDto tenant = remoteTenantService.getTenantByNo(payOrder.getTenantCode());
        if (tenant == null) {
            throw new ServiceException("获取商户信息失败");
        }

        if (tenant.getValidBalance().compareTo(payOrder.getValidAmount()) < 0) {
            ChannelRetMsg channelRetMsg = ChannelRetMsg.confirmFail("400", "商户余额不足");
            this.updateInitOrderStateThrowException(OrderStatus.FAIL.getCode(), payOrder, channelRetMsg);
            throw new ServiceException("商户余额不足");
        }

        //商户添加冻结金额
        remoteTenantService.balanceFreeze(payOrder.getTenantId(), payOrder.getValidAmount());
        remoteBossService.insertFlow(DataFlowDto.buildFreeBalance(tenant, payOrder.getValidAmount()));
        
        //调起上游支付接口
        UnifiedOrderRS bizRS = (UnifiedOrderRS) paymentService.pay(null, payOrder, tenant);

        //处理上游返回数据
        processChannelMsg(bizRS.getChannelRetMsg(), payOrder);
        WebsocketMessage message = WebsocketMessage.build(WebsocketMessage.MessageType.CHANNEL, "订单:" + payOrder.getOrderNo() + ",处理通道:" + payOrder.getWayName() + ",结果:" + bizRS.getChannelRetMsg().getChannelErrMsg());
        remoteMessageService.sendMessageTenant(tenant.getId(), message);

        return bizRS.getChannelRetMsg().getChannelState();
    }

    public PayWayDto queryParams(String tenantCode, String ifCode) {
        return remoteBossService.queryParams(tenantCode, ifCode);
    }
}
