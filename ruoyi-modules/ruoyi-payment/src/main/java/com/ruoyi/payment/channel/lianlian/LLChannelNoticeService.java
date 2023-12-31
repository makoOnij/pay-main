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
package com.ruoyi.payment.channel.lianlian;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.ChannelRetMsg;
import com.ruoyi.common.core.dto.PayOrderDto;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.payment.channel.AbstractChannelNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;


@Service
@Slf4j
public class LLChannelNoticeService extends AbstractChannelNoticeService {

    @Override
    public String getIfCode() {
        return "LL";
    }

    /**
     * 转换参数
     *
     * @param request
     * @param urlOrderId
     * @param noticeTypeEnum
     * @return
     */
    @Override
    public MutablePair<String, Object> parseParams(HttpServletRequest request, String urlOrderId, NoticeTypeEnum noticeTypeEnum) {
        try {

            JSONObject params = getReqParamJSON();
            String payOrderId = params.getString("appOrderNo");
            return MutablePair.of(payOrderId, params);

        } catch (Exception e) {
            log.error("error", e);
            throw new ServiceException("ERROR");
        }
    }

    @Override
    public ChannelRetMsg doNotice(HttpServletRequest request, Object params, PayOrderDto payOrderDto, TenantDto tenantDto, NoticeTypeEnum noticeTypeEnum) {
        try {

            ChannelRetMsg result = ChannelRetMsg.confirmSuccess(null);

            String logPrefix = "【处理连连代付回调】";

            // 获取请求参数
            JSONObject jsonParams = (JSONObject) params;
            log.info("{} 回调参数, jsonParams：{}", logPrefix, jsonParams);

            // 校验支付回调
            boolean verifyResult = verifyParams(jsonParams, payOrderDto, tenantDto);
            // 验证参数失败
            if (!verifyResult) {
                throw new ServiceException("ERROR");
            }
            log.info("{}验证支付通知数据及签名通过", logPrefix);

            //验签成功后判断上游订单状态
            ResponseEntity okResponse = textResp("SUCCESS");
            result.setResponseEntity(okResponse);
            return result;

        } catch (Exception e) {
            log.error("error", e);
            throw new ServiceException("ERROR");
        }
    }

    /**
     * 验证云闪付支付通知参数
     *
     * @return
     */
    public boolean verifyParams(JSONObject jsonParams, PayOrderDto payOrderDto, TenantDto tenantDto) {

        String orderNo = jsonParams.getString("appOrderNo");        // 商户订单号
        String txnAmt = jsonParams.getString("orderAmt");        // 支付金额
        if (StringUtils.isEmpty(orderNo)) {
            log.info("订单ID为空 [orderNo]={}", orderNo);
            return false;
        }
        if (StringUtils.isEmpty(txnAmt)) {
            log.info("金额参数为空 [txnAmt] :{}", txnAmt);
            return false;
        }
        // 核对金额

        if (payOrderDto.getAmount().equals(BigDecimal.valueOf(Double.parseDouble(txnAmt)))) {
            log.info("订单金额与参数金额不符。 dbPayAmt={}, txnAmt={}, payOrderId={}", payOrderDto.getAmount(), txnAmt, orderNo);
            return false;
        }
        return true;
    }

}
