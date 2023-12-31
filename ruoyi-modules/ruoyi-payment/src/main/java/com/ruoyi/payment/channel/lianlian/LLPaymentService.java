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

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.boss.api.domain.PayWayDto;
import com.ruoyi.common.core.domain.ChannelRetMsg;
import com.ruoyi.common.core.dto.PayOrderDto;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.common.core.utils.PayKit;
import com.ruoyi.payment.channel.AbstractPaymentService;
import com.ruoyi.payment.params.AbstractRS;
import com.ruoyi.payment.params.UnifiedOrderRQ;
import com.ruoyi.payment.params.UnifiedOrderRS;
import com.ruoyi.payment.service.PayOrderProcessService;
import com.ruoyi.payment.util.ApiResBuilder;
import com.ruoyi.system.api.RemoteConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 云闪付下单
 *
 * @author pangxiaoyu
 * @site https://www.jeequan.com
 * @date 2021-06-07 07:15
 */
@Service
@Slf4j
public class LLPaymentService extends AbstractPaymentService {

    @Autowired
    protected PayOrderProcessService payOrderProcessService;

    @DubboReference
    protected RemoteConfigService remoteConfigService;

    @Override
    public String getIfCode() {
        return "LL";
    }

    @Override
    public boolean isSupport(String wayCode) {
        return false;
    }

    @Override
    public String preCheck(UnifiedOrderRQ rq, PayOrderDto payOrder) {
        return null;
    }

    @Override
    public AbstractRS pay(UnifiedOrderRQ rq, PayOrderDto payOrder, TenantDto tenantDto) {
        String logPrefix = "【连连代付】";
        JSONObject reqParams = new JSONObject();
        UnifiedOrderRS res = ApiResBuilder.buildSuccess(UnifiedOrderRS.class);
        ChannelRetMsg channelRetMsg = new ChannelRetMsg();
        res.setChannelRetMsg(channelRetMsg);

        // 请求参数赋值
        paramsSet(reqParams, payOrder);

        try {
            // 发送请求并返回订单状态
            JSONObject resJSON = packageParamAndReq("/newbankPay/crtAgencyOrder.do", reqParams, logPrefix, tenantDto);
            //请求 & 响应成功， 判断业务逻辑
            String respCode = resJSON.getString("code"); //应答码
            String respMsg = resJSON.getString("msg"); //应答信息
            //0000表示下单成功，其他均为失败
            if (respCode.equals("0000")) {
                //付款信息
                res.setMessage("成功");
                channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.WAITING);
            } else {
                res.setMessage(respMsg);
                channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
                channelRetMsg.setChannelErrCode("400");
                channelRetMsg.setChannelErrMsg(respMsg);
            }
        } catch (Exception e) {
            channelRetMsg.setChannelErrCode("400");
            channelRetMsg.setChannelErrMsg("系统维护");
        }
        return res;

    }


    /**
     * 封装参数 & 统一请求
     **/
    public JSONObject packageParamAndReq(String apiUri, JSONObject reqParams, String logPrefix, TenantDto tenantDto) {
        //查询商户号配置
        PayWayDto isvParams = payOrderProcessService.queryParams(tenantDto.getCode(), getIfCode());
        reqParams.put("appId", isvParams.getMchId()); // 商户号
        log.info("{} reqJSON={}", logPrefix, reqParams);
        String sign = PayKit.getSign(reqParams, isvParams.getMchKey()).toUpperCase();
        reqParams.put("sign", sign);
        String resText = HttpUtil.post(isvParams.getMchDomain() + apiUri, reqParams);
        return JSONObject.parseObject(resText);
    }


    /**
     * 云闪付 jsapi下单请求统一发送参数
     **/
    public void paramsSet(JSONObject reqParams, PayOrderDto payOrderDto) {
        //获取订单类型
        reqParams.put("appOrderNo", payOrderDto.getOrderNo());
        reqParams.put("orderAmt", payOrderDto.getAmount());
        reqParams.put("payId", "401");
        reqParams.put("accNo", payOrderDto.getBankAccount());
        reqParams.put("accName", payOrderDto.getUserName());
        reqParams.put("bankName", payOrderDto.getBankName());
        reqParams.put("notifyURL", remoteConfigService.getConfigValue("system.domain") + getNotifyUrl()); //交易通知地址

    }

}
