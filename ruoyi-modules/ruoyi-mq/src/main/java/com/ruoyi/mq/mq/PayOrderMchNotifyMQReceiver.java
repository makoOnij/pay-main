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

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.ruoyi.common.core.dto.TenantNotifyDto;
import com.ruoyi.common.core.exception.BizException;
import com.ruoyi.mq.IMQSender;
import com.ruoyi.mq.api.model.PayOrderMchNotifyMQ;
import com.ruoyi.order.api.RemoteOrderService;
import com.ruoyi.system.api.RemoteTenantService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 接收MQ消息
 * 业务： 支付订单商户通知
 *
 * @author terrfly
 * @site https://www.jeequan.com
 * @date 2021/7/27 9:23
 */
@Slf4j
@Component
public class PayOrderMchNotifyMQReceiver implements PayOrderMchNotifyMQ.IMQReceiver {

    @DubboReference
    private RemoteOrderService payOrderService;
    @DubboReference
    private RemoteTenantService mchNotifyRecordService;
    @Autowired
    private IMQSender mqSender;

    @Override
    public void receive(PayOrderMchNotifyMQ.MsgPayload payload) {

        try {
            log.info("接收商户通知MQ, msg={}", payload.toString());

            Long notifyId = payload.getNotifyId();
            TenantNotifyDto record = mchNotifyRecordService.getNotifyById(notifyId);
            if (record == null || record.getState() != TenantNotifyDto.STATE_ING) {
                log.info("查询通知记录不存在或状态不是通知中");
                return;
            }
            if (record.getNotifyCount() >= 6) {
                log.info("已达到最大发送次数");
                return;
            }

            //1. (发送结果最多6次)
            Long currentCount = record.getNotifyCount() + 1;

            String notifyUrl = record.getNotifyUrl();
            String res = "";
            try {
                int pathEndPos = notifyUrl.indexOf('?');
                if (pathEndPos <= -1) {
                    log.error("通知地址错误，参数为空，notifyUrl：{}", notifyUrl);
                    throw new BizException("通知地址错误");
                }

                res = HttpUtil.post(StrUtil.subPre(notifyUrl, pathEndPos), StrUtil.subSuf(notifyUrl, pathEndPos + 1), 20000);
            } catch (Exception e) {
                log.error("http error", e);
                res = "连接[" + UrlBuilder.of(notifyUrl).getHost() + "]异常:【" + e.getMessage() + "】";
            }

            //支付订单 & 第一次通知: 更新为已通知
            if (currentCount == 1 && TenantNotifyDto.TYPE_PAY_ORDER == record.getOrderType()) {
                payOrderService.updateNotifySent(record.getOrderId());
            }

            //通知成功
            if ("SUCCESS".equalsIgnoreCase(res)) {
                mchNotifyRecordService.updateNotifyResult(notifyId, TenantNotifyDto.STATE_SUCCESS, res);
                return;
            }

            //通知次数 >= 最大通知次数时， 更新响应结果为异常， 不在继续延迟发送消息
            if (currentCount >= record.getNotifyCountLimit()) {
                mchNotifyRecordService.updateNotifyResult(notifyId, TenantNotifyDto.STATE_FAIL, res);
                return;
            }

            // 继续发送MQ 延迟发送
            mchNotifyRecordService.updateNotifyResult(notifyId, TenantNotifyDto.STATE_ING, res);
            // 通知延时次数
            //        1   2  3  4   5   6
            //        0  30 60 90 120 150
            mqSender.send(PayOrderMchNotifyMQ.build(notifyId), currentCount.intValue() * 30);


        } catch (Exception e) {
            log.error(e.getMessage(), e);

        }
    }
}
