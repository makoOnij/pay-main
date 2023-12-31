package com.ruoyi.boss.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 代付通道对象 merchant_way
 *
 * @author ruoyi
 * @date 2023-11-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tenant_way")
public class TenantChannel extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     *
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 商户ID
     */
    private Long tenantId;
    /**
     * 通道ID
     */
    private Long payId;
    /**
     *
     */
    private String tenantName;
    /**
     *
     */
    private String payName;
    /**
     *
     */
    private String payCode;
    /**
     *
     */
    private BigDecimal rate;
    /**
     *
     */
    private BigDecimal fee;
    /**
     * 通道名称
     */
    private String name;
    /**
     * 权重
     */
    private Long sort;
    /**
     * 币种
     */
    private String currency;
    /**
     * 代付方式
     */
    private Integer wayType;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 最大限额
     */
    private BigDecimal maxAmount;
    /**
     * 最小限额
     */
    private BigDecimal minAmount;
    /**
     * 固定金额
     */
    private BigDecimal fixedAmount;

}
