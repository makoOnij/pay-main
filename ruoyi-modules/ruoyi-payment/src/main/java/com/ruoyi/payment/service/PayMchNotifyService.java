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

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.dto.PayOrderDto;
import com.ruoyi.common.core.dto.TenantNotifyDto;
import com.ruoyi.common.core.utils.PayKit;
import com.ruoyi.common.core.utils.StringKit;
import com.ruoyi.mq.api.RemoteMQService;
import com.ruoyi.mq.api.model.PayOrderMchNotifyMQ;
import com.ruoyi.payment.params.QueryPayOrderRS;
import com.ruoyi.system.api.RemoteTenantService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/*
 * 商户通知 service
 *
 * @author terrfly
 * @site https://www.jeequan.com
 * @date 2021/6/8 17:43
 */
@Slf4j
@Service
public class PayMchNotifyService {

    @DubboReference
    private RemoteTenantService tenantService;

    @DubboReference
    private RemoteMQService remoteMQService;
    /**
     * 商户通知信息， 只有订单是终态，才会发送通知， 如明确成功和明确失败
     **/
    public void payOrderNotify(PayOrderDto dbPayOrder) {

        try {
            // 通知地址为空
            if (StringUtils.isEmpty(dbPayOrder.getNotifyUrl())) {
                return;
            }

            //获取到通知对象
            TenantNotifyDto mchNotifyRecord = tenantService.findNotifyByOrderId(dbPayOrder.getId());

            if (mchNotifyRecord != null) {

                log.info("当前已存在通知消息， 不再发送。");
                return;
            }

            //商户app私钥
            String appSecret = tenantService.getTenantByNo(dbPayOrder.getTenantCode()).getSecret();

            // 封装通知url
            String notifyUrl = createNotifyUrl(dbPayOrder, appSecret);
            mchNotifyRecord = new TenantNotifyDto();
            mchNotifyRecord.setOrderId(dbPayOrder.getId());
            mchNotifyRecord.setOrderType(TenantNotifyDto.TYPE_PAY_ORDER);
            mchNotifyRecord.setTenantName(dbPayOrder.getTenantCode());
            mchNotifyRecord.setMchOrderId(dbPayOrder.getAppOrderNo()); //商户订单号
            mchNotifyRecord.setNotifyUrl(notifyUrl);
            mchNotifyRecord.setResResult("");
            mchNotifyRecord.setNotifyCount(0L);
            mchNotifyRecord.setState(TenantNotifyDto.STATE_ING); // 通知中


            tenantService.saveNotify(mchNotifyRecord);


            //推送到MQ
            Long notifyId = mchNotifyRecord.getNotifyId();
            remoteMQService.send(PayOrderMchNotifyMQ.build(notifyId));

        } catch (Exception e) {
            log.error("推送失败！", e);
        }
    }


    /**
     * 创建响应URL
     */
    public String createNotifyUrl(PayOrderDto payOrder, String appSecret) {

        QueryPayOrderRS queryPayOrderRS = QueryPayOrderRS.buildByPayOrder(payOrder);
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(queryPayOrderRS);
        jsonObject.put("reqTime", System.currentTimeMillis()); //添加请求时间

        // 报文签名
        jsonObject.put("sign", PayKit.getSign(jsonObject, appSecret));

        // 生成通知
        return StringKit.appendUrlQuery(payOrder.getNotifyUrl(), jsonObject);
    }

}
