package com.ruoyi.boss.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 资金明细对象 data_flow
 *
 * @author ruoyi
 * @date 2023-11-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_flow")
public class DataFlow extends BaseTenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 序列号
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 总资产
     */
    private BigDecimal userAmount;
    /**
     * 用户号
     */
    private String userAccount;
    /**
     * 当前余额
     */
    private BigDecimal currencyAmount;
    /**
     * 原余额
     */
    private BigDecimal beforeAmount;
    /**
     * 操作金额
     */
    private BigDecimal amount;
    /**
     * 当前冻结
     */
    private BigDecimal beforeFreeAmount;
    /**
     * 原冻结
     */
    private BigDecimal freeAmount;
    /**
     * 手续费
     */
    private BigDecimal fee;
    /**
     * 交易流水号
     */
    private String orderNo;
    /**
     * 0 充值 1支付 2提现 3代付 4 手续费
     */
    private Integer flowType;

    private Date createTime;


}
