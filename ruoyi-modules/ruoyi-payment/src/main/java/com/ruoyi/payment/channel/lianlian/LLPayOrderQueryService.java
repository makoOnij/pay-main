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
import com.ruoyi.payment.channel.IPayOrderQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 云闪付查单
 */
@Service
@Slf4j
public class LLPayOrderQueryService implements IPayOrderQueryService {

    @Override
    public String getIfCode() {
        return "LL";
    }

    @Autowired
    private LLPaymentService LLPaymentService;

    @Override
    public ChannelRetMsg query(PayOrderDto payOrderDto, TenantDto tenantDto) {
        JSONObject reqParams = new JSONObject();
        String orderType = "1";
        String logPrefix = "【连连代付(" + orderType + ")查单】";

        try {
            reqParams.put("appOrderNo", payOrderDto.getOrderNo()); //订单号
            //封装公共参数 & 签名 & 调起http请求 & 返回响应数据并包装为json格式。
            JSONObject resJSON = LLPaymentService.packageParamAndReq("/newbankPay/selAgencyOrder.do", reqParams, logPrefix, null);

            if (resJSON == null) {
                return ChannelRetMsg.waiting(); //支付中
            }

            //请求 & 响应成功， 判断业务逻辑
            String respCode = resJSON.getString("code"); //应答码

            if (("0000").equals(respCode)) {//如果查询交易成功
                //交易成功，更新商户订单状态
                return ChannelRetMsg.confirmSuccess(resJSON.getJSONObject("data").getString("orderNo"));  //支付成功
            }
            return ChannelRetMsg.waiting(); //支付中
        } catch (Exception e) {
            return ChannelRetMsg.waiting(); //支付中
        }
    }

}
