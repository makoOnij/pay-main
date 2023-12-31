package com.ruoyi.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 代付订单对象 order_payfor
 *
 * @author ruoyi
 * @date 2023-11-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pay_order")
public class OrderPay extends BaseTenantEntity {

    private static final long serialVersionUID=1L;

    /**
     *
     */
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 商户编号
     */
    private String tenantCode;
    /**
     * 系统订单号
     */
    private String orderNo;
    /**
     * 商户订单号
     */
    private String appOrderNo;

    /**
     * 商户手续费
     */
    private BigDecimal payforFee;
    private BigDecimal payforRate;


    /**
     * 用户名
     */
    private String userName;
    /**
     * 银行卡号
     */
    private String bankAccount;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 状态0 待处理 1已发送 2 回调成功 3回调失败
     */
    private Integer status;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 回调时间
     */
    private Date backTime;

    /**
     * 通道名称
     */
    private String wayName;
    /**
     * 通道编码
     */
    private String wayCode;
    /**
     * 通道手续费
     */
    private BigDecimal wayFee;
    private BigDecimal wayRate;
    
    private String errorCode;
    private String errorMessage;
    private String parentName;
    private Long parentId;


}
