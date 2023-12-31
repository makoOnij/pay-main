package com.ruoyi.boss.api.domain;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TenantWayDto implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     *
     */
    private Long id;

    private Long payId;
    /**
     * 通道名称
     */
    private String name;
    /**
     * 渠道金额
     */
    private BigDecimal amount;
    /**
     *
     */
    private Long tenantId;
    /**
     *
     */
    private String tenantName;
    /**
     * 通道编码
     */
    private String payCode;

    private String payName;
    /**
     * 状态0 开启 1关闭
     */
    private Integer status;
    /**
     * 通道汇率
     */
    private BigDecimal rate;
    /**
     * 通道返点
     */
    private BigDecimal refund;
    /**
     * 备注
     */
    private String ramark;

    private String currency;

    private BigDecimal maxAmount;

    private BigDecimal minAmount;

    private BigDecimal fixedAmount;

    private BigDecimal fee;

    private Integer wayType;

}
