package com.ruoyi.order.api;

import com.ruoyi.common.core.dto.PayOrderChannelDto;
import com.ruoyi.common.core.dto.PayOrderDto;
import com.ruoyi.order.api.dto.OrderChargeDto;

/**
 * 订单服务
 *
 * @author Lion Li
 */
public interface RemoteOrderService {

    /**
     * 查询订单是否存在
     * @param mchNo
     * @param appOrderNo
     * @return
     */
    Boolean exitsOrder(String mchNo, String appOrderNo);

    boolean updateInit2Ing(String orderNo, PayOrderDto payOrderDto);

    boolean updateIng2SuccessOrFail(Long id, Byte status, String channelOrderId, String channelUserId, String channelErrCode, String channelErrMsg);

    PayOrderDto getByOrderNo(String orderNo);

    /**
     * 根据ID获取订单信息
     *
     * @param orderId
     * @return
     */
    PayOrderDto getById(Long orderId);

    /**
     * 查询上游支付通道信息
     *
     * @param code
     * @return
     */
    PayOrderChannelDto getChannelByCode(String code, Long orderId);




    boolean updateIng2Success(String orderNo);

    boolean updateIng2Fail(String orderNo, String channelErrCode, String channelErrMsg);

    void confirmSuccess(PayOrderDto payOrderDto);

    PayOrderDto queryMchOrder(String mchNo, String payOrderId, String mchOrderNo);

    void updateInit2Close(Long id);

    void updateIng2Close(Long payOrderId);

    /**
     * 创建充值订单
     *
     */
    void addOrder(OrderChargeDto dto);

    /**
     * 创建订单
     *
     * @param model
     */
    Long addOrder(PayOrderDto model);

    void updateNotifySent(Long orderId);

    void changeChannel(Long id);
}
