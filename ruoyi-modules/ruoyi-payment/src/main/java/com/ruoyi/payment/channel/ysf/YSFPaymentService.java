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
package com.ruoyi.payment.channel.ysf;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.ChannelRetMsg;
import com.ruoyi.common.core.dto.PayOrderDto;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.payment.channel.AbstractPaymentService;
import com.ruoyi.payment.params.AbstractRS;
import com.ruoyi.payment.params.UnifiedOrderRQ;
import com.ruoyi.payment.params.UnifiedOrderRS;
import com.ruoyi.payment.service.PayOrderProcessService;
import com.ruoyi.payment.util.ApiResBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 云闪付下单
 *
 * @author pangxiaoyu
 * @site https://www.jeequan.com
 * @date 2021-06-07 07:15
 */
@Service
@Slf4j
public class YSFPaymentService extends AbstractPaymentService {
    @Autowired
    protected PayOrderProcessService remoteBossService;
    @Override
    public String getIfCode() {
        return "YSF";
    }

    @Override
    public boolean isSupport(String wayCode) {
        return false;
    }

    @Override
    public String preCheck(UnifiedOrderRQ rq, PayOrderDto payOrder) {
        // return PaywayUtil.getRealPaywayService(this, payOrder.getWayCode()).preCheck(rq, payOrder);
        return null;
    }

    @Override
    public AbstractRS pay(UnifiedOrderRQ rq, PayOrderDto payOrder, TenantDto tenantDto) {
        String logPrefix = "【云闪付(unionpay)jsapi支付】";
        JSONObject reqParams = new JSONObject();
        UnifiedOrderRS res = ApiResBuilder.buildSuccess(UnifiedOrderRS.class);
        ChannelRetMsg channelRetMsg = new ChannelRetMsg();
        res.setChannelRetMsg(channelRetMsg);

        // 请求参数赋值
        jsapiParamsSet(reqParams, payOrder, getNotifyUrl(), getReturnUrl());
        //云闪付扫一扫支付， 需要传入termInfo参数
        reqParams.put("termInfo", "{\"ip\": \"" + StringUtils.defaultIfEmpty(payOrder.getClientIp(), "127.0.0.1") + "\"}");

        //客户端IP
        reqParams.put("customerIp", StringUtils.defaultIfEmpty(payOrder.getClientIp(), "127.0.0.1"));
        // 发送请求并返回订单状态
        JSONObject resJSON = packageParamAndReq("/gateway/api/pay/unifiedorder", reqParams, logPrefix, tenantDto);
        //请求 & 响应成功， 判断业务逻辑
//        String respCode = resJSON.getString("respCode"); //应答码
//        String respMsg = resJSON.getString("respMsg"); //应答信息

        try {
            //00-交易成功， 02-用户支付中 , 12-交易重复， 需要发起查询处理    其他认为失败
            if ("00".equals("00")) {
                //付款信息
                res.setMessage("成功");
                channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.WAITING);
            } else {
                channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
                channelRetMsg.setChannelErrCode("400");
                channelRetMsg.setChannelErrMsg("系统维护");
            }
        } catch (Exception e) {
            channelRetMsg.setChannelErrCode("400");
            channelRetMsg.setChannelErrMsg("系统维护");
        }
        return res;
        //return PaywayUtil.getRealPaywayService(this, payOrder.getWayCode()).pay(rq, payOrder, tenantDto);
    }


    /** 封装参数 & 统一请求 **/
    public JSONObject packageParamAndReq(String apiUri, JSONObject reqParams, String logPrefix, TenantDto tenantDto) {
//        Map<String,Object> isvParams =remotePayService.queryParams(tenantDto.getTenantCode(), getIfCode());
//
//        reqParams.put("merId", isvParams.get("merId")); // 商户号


        log.info("{} reqJSON={}", logPrefix, reqParams);
        //String resText = HttpUtil.post( apiUri, reqParams);


        return new JSONObject();

    }


    /**
     * 云闪付 jsapi下单请求统一发送参数
     **/
    public static void jsapiParamsSet(JSONObject reqParams, PayOrderDto payOrder, String notifyUrl, String returnUrl) {

        reqParams.put("orderType", 0); //订单类型： alipayJs-支付宝， wechatJs-微信支付， upJs-银联二维码
        ysfPublicParams(reqParams, payOrder);
        reqParams.put("backUrl", notifyUrl); //交易通知地址
        reqParams.put("frontUrl", returnUrl); //前台通知地址
    }

    /** 云闪付公共参数赋值 **/
    public static void ysfPublicParams(JSONObject reqParams, PayOrderDto payOrderDto) {
        //获取订单类型
        reqParams.put("orderNo", payOrderDto.getAppOrderNo()); //订单号
        reqParams.put("orderTime", DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN)); //订单时间 如：20180702142900
        reqParams.put("txnAmt", payOrderDto.getAmount()); //交易金额 单位：分，不带小数点
        reqParams.put("currencyCode", "156"); //交易币种 不出现则默认为人民币-156
        reqParams.put("orderInfo", payOrderDto.getAppOrderNo()); //订单信息 订单描述信息，如：京东生鲜食品
    }
}
