package com.ruoyi.payment.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.boss.api.RemoteBossService;
import com.ruoyi.common.core.domain.BaseModel;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.common.core.enums.TenantStatus;
import com.ruoyi.common.core.exception.BizException;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.PayKit;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.order.api.RemoteOrderService;
import com.ruoyi.payment.params.AbstractMchAppRQ;
import com.ruoyi.payment.params.AbstractRQ;
import com.ruoyi.payment.service.PayOrderProcessService;
import com.ruoyi.payment.service.RequestKitBean;
import com.ruoyi.payment.service.ValidateService;
import com.ruoyi.resource.api.RemoteMessageService;
import com.ruoyi.system.api.RemoteTenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Validated
@RequiredArgsConstructor
@Slf4j
public class ApiController extends BaseController {
    @Autowired
    protected RequestKitBean requestKitBean;
    @Autowired
    protected ValidateService validateService;

    @DubboReference
    protected RemoteBossService remoteBossService;
    @DubboReference
    protected RemoteTenantService remoteTenantService;
    @DubboReference
    protected RemoteOrderService remoteOrderService;
    @Autowired
    protected PayOrderProcessService payOrderProcessService;

    @DubboReference
    protected RemoteMessageService remoteMessageService;

    /**
     * 获取请求参数并转换为对象，商户通用验证
     **/
    protected <T extends AbstractRQ> T getRQByWithMchSign(Class<T> cls) {

        //获取请求RQ, and 通用验证
        T bizRQ = getRQ(cls);

        AbstractMchAppRQ abstractMchAppRQ = (AbstractMchAppRQ) bizRQ;

        //业务校验， 包括： 验签， 商户状态是否可用， 是否支持该支付方式下单等。
        String mchNo = abstractMchAppRQ.getMchNo();
        String sign = bizRQ.getSign();

        if (StringUtils.isAnyBlank(mchNo, sign)) {
            throw new ServiceException("参数有误！");
        }
        //获取支付参数 (缓存数据) 和 商户信息
        TenantDto mchInfo = remoteTenantService.getTenantByNo(mchNo);
        if (mchInfo == null) {
            throw new ServiceException("获取商户应用信息失败");
        }
        if (mchInfo.getStatus().equals(TenantStatus.CLOSE.getCode())) {
            throw new ServiceException("商户信息不存在或商户状态不可用");
        }
        // 验签
        String appSecret = mchInfo.getSecret();

        if (!validSign(bizRQ, appSecret)) {
            throw new BizException("验签失败");
        }

        return bizRQ;
    }

    /**
     * 验签
     *
     * @param data
     * @param key
     * @param <M>
     * @return
     */
    public <M> Boolean validSign(M data, String key) {
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(data, "mchNo", "sign", "appOrderNo", "code", "amount", "userName", "bankAccount", "bankName", "notifyUrl", "reqTime");
        String sign = (String) stringObjectMap.get("sign");
        stringObjectMap.remove("sign");
        return PayKit.verifySign(stringObjectMap, key, sign);
    }

    /**
     * 创建签名
     *
     * @param data
     * @param key
     * @param <M>
     * @return
     */
    public <M> String buildSign(M data, String key) {
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(data);
        stringObjectMap.remove("sign");
        stringObjectMap.put("key", key);
        return SecureUtil.signParams(DigestAlgorithm.MD5, stringObjectMap, "&", "=", true);
    }

    protected JSONObject getReqParamJSON() {
        return requestKitBean.getReqParamJSON();
    }

    /**
     * 获取请求参数并转换为对象，通用验证
     **/
    protected <T extends AbstractRQ> T getRQ(Class<T> cls) {

        T bizRQ = getObject(cls);

        // [1]. 验证通用字段规则
        validateService.validate(bizRQ);

        return bizRQ;
    }

    /**
     * 获取对象类型
     **/
    protected <T> T getObject(Class<T> clazz) {

        JSONObject params = getReqParamJSON();
        T result = params.toJavaObject(clazz);

        if (result instanceof BaseModel) {  //如果属于BaseModel, 处理apiExtVal
            JSONObject resultTemp = (JSONObject) JSON.toJSON(result);
            for (Map.Entry<String, Object> entry : params.entrySet()) {  //遍历原始参数
                if (!resultTemp.containsKey(entry.getKey())) {
                    ((BaseModel) result).addExt(entry.getKey(), entry.getValue());
                }
            }
        }

        return result;
    }

}
