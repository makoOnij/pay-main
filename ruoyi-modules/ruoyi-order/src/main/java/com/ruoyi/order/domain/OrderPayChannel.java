package com.ruoyi.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 代付订单对象 order_payfor
 *
 * @author ruoyi
 * @date 2023-11-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pay_order_channel")
public class OrderPayChannel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long orderId;
    private String orderNo;
    private Long wayId;
    private String wayCode;
    private String errorCode;
    private String errorMsg;
    private Integer retryCount;
    /**
     * 状态0 待处理  1 回调成功 2回调失败
     */
    private Integer status;


}
