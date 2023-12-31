package com.ruoyi.boss.domain;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 财务报对象 data_report
 *
 * @author ruoyi
 * @date 2023-11-26
 */
@Data
@TableName("data_report")
public class DataReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ExcelProperty(value = "商户号")
    private String tenantName;

    /**
     * 商户ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ExcelIgnore
    private Long tenantId;
    /**
     * 序号
     */
    @TableId(value = "id")
    private Long id;

    private LocalDate createTime;

    /**
     * 总资产
     */
    private BigDecimal totalAmount;
    /**
     * 余额
     */
    private BigDecimal totalBalance;
    /**
     * 冻结
     */
    private BigDecimal totalFreeze;
    /**
     * 充值
     */
    private BigDecimal totalCharge;
    /**
     * 提现
     */
    private BigDecimal totalWithdraw;
    /**
     * 代付
     */
    private BigDecimal totalPayfor;

    private Long totalPayforCount;
    /**
     * 订单数
     */
    private Long totalOrderCount;

    /**
     * 订单金额
     */
    private BigDecimal totalOrderAmount;
    /**
     * 手续费
     */
    private BigDecimal totalFee;
    /**
     * 代付费率%
     */
    private BigDecimal totalPayforRate;
    /**
     * 代付附加费
     */
    private BigDecimal totalPayforFee;

}
