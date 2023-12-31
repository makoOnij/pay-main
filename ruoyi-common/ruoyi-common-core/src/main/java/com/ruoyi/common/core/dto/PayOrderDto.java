package com.ruoyi.common.core.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 代付订单对象 order_payfor
 *
 * @author ruoyi
 * @date 2023-11-22
 */

@Data
public class PayOrderDto implements Serializable {

    private static final long serialVersionUID=1L;

    private String orderNo;

    private String tenantCode;
    private String tenantName;
    private Long tenantId;

    private Long id;
    /**
     * 系统订单号
     */
    @NotNull(message = "商户订单号不能为空")
    private String appOrderNo;


    /**
     * 用户名
     */
    @NotNull(message = "出款账号不能为空")
    private String userName;
    /**
     * 银行卡号
     */
    @NotNull(message = "银行账号不能为空")
    private String bankAccount;
    /**
     * 银行名称
     */
    @NotNull(message = "银行名称不能为空")
    private String bankName;


    @NotNull(message = "金额不能为空")
    private BigDecimal amount;
    @NotNull(message = "通道编码不能为空")
    private String wayCode;

    private Long wayId;

    private String wayName;


    /**
     * 商户手续费
     */

    private BigDecimal payforFee;

    private BigDecimal payforRate;
    /**
     * 通道手续费
     */
    private BigDecimal wayFee;
    private BigDecimal wayRate;


    /**
     * 货币
     */
    private String currency;

    /** 异步通知地址 **/
    private String notifyUrl;

    /** 跳转通知地址 **/
    private String returnUrl;

    private Date ExpiredTime;

    private String createBy;

    private Date createTime;

    private Integer status;

    private String errorCode;
    private String errorMessage;

    private String clientIp;

    private Date successTime;

    private String parentName;
    private Long parentId;

    public BigDecimal getValidAmount() {
        return amount.add(wayFee).add(payforFee);
    }

    private BigDecimal validAmount;

}
