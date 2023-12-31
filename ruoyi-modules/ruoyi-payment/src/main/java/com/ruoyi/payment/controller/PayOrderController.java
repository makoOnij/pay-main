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
package com.ruoyi.payment.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.ruoyi.boss.api.domain.TenantWayDto;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.dto.PayOrderDto;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.common.core.enums.AutoPayEnum;
import com.ruoyi.common.core.enums.OpenStatus;
import com.ruoyi.common.core.enums.OrderStatus;
import com.ruoyi.common.core.enums.TenantStatus;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.SeqKit;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.mq.api.RemoteMQService;
import com.ruoyi.mq.api.model.PayOrderChannelMQ;
import com.ruoyi.payment.channel.IPaymentService;
import com.ruoyi.payment.params.UnifiedOrderRQ;
import com.ruoyi.payment.params.UnifiedOrderRS;
import com.ruoyi.resource.api.domain.WebsocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

/*
 * 创建支付订单抽象类
 */
@Slf4j
@RestController
public class PayOrderController extends ApiController {

    @DubboReference
    private RemoteMQService remoteMQService;

    /**
     * 统一下单接口
     **/
    @PostMapping("/api/order")
    public R Order() {
        //获取参数 & 验签
        UnifiedOrderRQ bizRQ = getRQByWithMchSign(UnifiedOrderRQ.class);

        //实现子类的res
        R apiRes = unifiedOrder(bizRQ);
        if (apiRes.getData() == null) {
            return apiRes;
        }

        UnifiedOrderRS bizRes = (UnifiedOrderRS) apiRes.getData();

        return R.ok(bizRes);
    }

    /**
     * 统一下单 (新建订单模式)
     **/
    protected R unifiedOrder(UnifiedOrderRQ bizRQ) {
        return unifiedOrder(bizRQ, null);
    }

    /**
     * 统一下单
     **/
    public R unifiedOrder(UnifiedOrderRQ bizRQ, PayOrderDto payOrderDto) {


        // 响应数据
        UnifiedOrderRS bizRS = null;


        String mchNo = bizRQ.getMchNo();

        //5分钟订单超时
        if (System.currentTimeMillis() - Long.parseLong(bizRQ.getReqTime()) > 60 * 5) {
            throw new ServiceException("订单已超时,不能大于5分钟");
        }

        //获取支付参数 (缓存数据) 和 商户信息
        TenantDto tenant = remoteTenantService.getTenantByNo(mchNo);
        if (tenant == null) {
            throw new ServiceException("获取商户信息失败");
        }
        if (tenant.getOpenApiWhiteList().equals(OpenStatus.OPEN.getCode())) {
            if (ObjectUtil.isNotEmpty(tenant.getApiWhiteList())) {
                Optional<String> any = Arrays.stream(tenant.getApiWhiteList().split(",")).filter(p -> p.equals(ServletUtils.getClientIP())).findAny();
                if (!any.isPresent()) {
                    throw new ServiceException("接口IP地址未过白,请联系管理员");
                }
            }
        }

        if (tenant.getMinAmount().compareTo(BigDecimal.ZERO) > 0) {
            if (payOrderDto.getAmount().compareTo(tenant.getMinAmount()) < 0) {
                throw new ServiceException("商户最小金额:" + tenant.getMinAmount());
            }
        }

        // 只有新订单模式，进行校验
        if (remoteOrderService.exitsOrder(mchNo, bizRQ.getAppOrderNo())) {
            throw new ServiceException("商户订单[" + bizRQ.getAppOrderNo() + "]已存在");
        }
        TenantWayDto wayByCode = null;
        //查询所有通道配置权重最高的通道
        if (ObjectUtil.isNotEmpty(bizRQ.getCode())) {
            wayByCode = remoteBossService.getBestWay(tenant.getId(), null);
        } else {
            wayByCode = remoteBossService.getWayByCode(bizRQ.getCode());
        }

        if (ObjectUtil.isEmpty(wayByCode)) {
            throw new ServiceException("支付通道不存在");
        }
        if (wayByCode.getStatus().equals(TenantStatus.CLOSE.getCode())) {
            throw new ServiceException("支付通道已关闭");
        }
        if (wayByCode.getFixedAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new ServiceException("支付通道固定金额:" + wayByCode.getFixedAmount());
        }
        if (wayByCode.getMaxAmount().compareTo(BigDecimal.ZERO) > 0) {
            if (payOrderDto.getAmount().compareTo(wayByCode.getMaxAmount()) > 0) {
                throw new ServiceException("支付通道金额不能大于:" + wayByCode.getMaxAmount());
            }
        }
        if (wayByCode.getMinAmount().compareTo(BigDecimal.ZERO) > 0) {
            if (payOrderDto.getAmount().compareTo(wayByCode.getMinAmount()) < 0) {
                throw new ServiceException("支付通道金额不能小于:" + wayByCode.getMinAmount());
            }
        }

        // 接口代码
        String ifCode = wayByCode.getPayCode();
        IPaymentService paymentService = SpringUtil.getBean(ifCode + "PaymentService", IPaymentService.class);
        if (paymentService == null) {
            throw new ServiceException("未支持此通道,请联系管理员");
        }


        //生成订单
        payOrderDto = genPayOrder(bizRQ, tenant, ifCode, wayByCode);

        BigDecimal totalAmount = payOrderDto.getAmount().add(tenant.getPayforFee()).add(payOrderDto.getWayFee());
        if (tenant.getValidBalance().compareTo(totalAmount) < 0) {
            throw new ServiceException("商户余额不足");
        }
        //预先校验
        String errMsg = paymentService.preCheck(bizRQ, payOrderDto);
        if (StringUtils.isNotEmpty(errMsg)) {
            throw new ServiceException(errMsg);
        }


        String newPayOrderId = paymentService.customPayOrderId(bizRQ, payOrderDto);


        if (StringUtils.isNotBlank(newPayOrderId)) { // 自定义订单号
            payOrderDto.setOrderNo(newPayOrderId);
        }
        payOrderDto.setWayId(wayByCode.getId());

        boolean toProcess = true;

        if (!tenant.getAutoPay().equals(AutoPayEnum.AUTO.getCode())) {
            payOrderDto.setStatus(OrderStatus.WAIT.getCode());
            toProcess = false;
        } else {
            if (tenant.getMaxAmount().compareTo(BigDecimal.ZERO) > 0) {
                if (payOrderDto.getAmount().compareTo(tenant.getMaxAmount()) > 0) {
                    payOrderDto.setStatus(OrderStatus.AUDIT.getCode());
                    toProcess = false;
                }
            }
        }
        //订单入库 订单状态： 生成状态  此时没有和任何上游渠道产生交互。
        Long orderId = remoteOrderService.addOrder(payOrderDto);
        payOrderDto.setId(orderId);

        if (toProcess) {
            //调起上游支付接口
            remoteMQService.send(PayOrderChannelMQ.build(orderId));
        }

        WebsocketMessage message = WebsocketMessage.build(WebsocketMessage.MessageType.ORDER, "您有新的代付订单");
        remoteMessageService.sendMessageTenant(tenant.getId(), message);
        return packageApiResByPayOrder(payOrderDto);


    }

    private PayOrderDto genPayOrder(UnifiedOrderRQ rq, TenantDto mchInfo, String ifCode, TenantWayDto wayDto) {

        PayOrderDto payOrderDto = new PayOrderDto();
        payOrderDto.setOrderNo(SeqKit.genPayOrderId()); //生成订单ID
        //填充商户信息
        payOrderDto.setTenantCode(rq.getMchNo()); //商户应用appId
        payOrderDto.setTenantId(mchInfo.getId());
        payOrderDto.setTenantName(mchInfo.getName());

        payOrderDto.setPayforFee(mchInfo.getPayforFee()); //订单手续费
        payOrderDto.setPayforRate(mchInfo.getPayforRate()); //订单手续费率
        //数据冗余做数据隔离
        payOrderDto.setCreateBy(mchInfo.getLoginUser());
        
        //渠道信息
        TenantDto channel = remoteTenantService.getChannel(mchInfo.getId());
        payOrderDto.setParentName(channel.getName());
        payOrderDto.setParentId(channel.getId());
        /**
         *设置订单信息
         */
        payOrderDto.setAmount(rq.getAmount()); //订单金额
        payOrderDto.setCurrency(rq.getCurrency()); //币种
        payOrderDto.setNotifyUrl(rq.getNotifyUrl()); //异步通知地址
        payOrderDto.setReturnUrl(rq.getReturnUrl()); //页面跳转地址
        payOrderDto.setUserName(rq.getUserName());
        payOrderDto.setBankAccount(rq.getBankAccount());
        payOrderDto.setBankName(rq.getBankName());
        payOrderDto.setAppOrderNo(rq.getAppOrderNo());
        /**
         * 设置通道信息
         */
        payOrderDto.setWayCode(ifCode); //接口代码
        payOrderDto.setWayName(wayDto.getName());
        payOrderDto.setWayFee(wayDto.getRate().multiply(payOrderDto.getAmount()));
        payOrderDto.setWayRate(wayDto.getRate());
        Date nowDate = new Date();

        //订单过期时间 单位： 秒
        if (rq.getExpiredTime() != null) {
            payOrderDto.setExpiredTime(DateUtil.offsetSecond(nowDate, rq.getExpiredTime()));
        } else {
            payOrderDto.setExpiredTime(DateUtil.offsetHour(nowDate, 2)); //订单过期时间 默认两个小时
        }
        payOrderDto.setCreateTime(nowDate); //订单创建时间
        payOrderDto.setStatus(OrderStatus.WAIT.getCode());// 默认状态已入库
        return payOrderDto;
    }


    /**
     * 统一封装订单数据
     **/
    private R packageApiResByPayOrder(PayOrderDto payOrderDto) {
        UnifiedOrderRS bizRS = new UnifiedOrderRS();
        // 返回接口数据
        bizRS.setOrderNo(payOrderDto.getOrderNo());
        bizRS.setOrderState(payOrderDto.getStatus());
        bizRS.setMchOrderNo(payOrderDto.getAppOrderNo());
        return R.ok(bizRS);
    }


}
