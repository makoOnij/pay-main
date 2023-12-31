package com.ruoyi.boss.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 代付通道视图对象
 *
 * @author ruoyi
 * @date 2023-11-24
 */
@Data
@ExcelIgnoreUnannotated
public class TenantChannelVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    @ExcelProperty(value = "")
    private Long id;

    /**
     * 商户ID
     */
    @ExcelProperty(value = "商户ID")
    private Long tenantId;

    /**
     * 通道ID
     */
    @ExcelProperty(value = "通道ID")
    private Long payId;

    /**
     * 
     */
    @ExcelProperty(value = "")
    private String tenantName;

    /**
     * 
     */
    @ExcelProperty(value = "")
    private String payName;

    /**
     * 
     */
    @ExcelProperty(value = "")
    private String payCode;

    /**
     * 
     */
    @ExcelProperty(value = "")
    private BigDecimal rate;

    /**
     * 
     */
    @ExcelProperty(value = "")
    private BigDecimal fee;

    /**
     * 通道名称
     */
    @ExcelProperty(value = "通道名称")
    private String name;

    /**
     * 权重
     */
    @ExcelProperty(value = "权重")
    private Long sort;

    /**
     * 币种
     */
    @ExcelProperty(value = "币种")
    private String currency;

    /**
     * 代付方式
     */
    @ExcelProperty(value = "代付方式")
    private Integer wayType;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态")
    private Integer status;

    /**
     * 最大限额
     */
    @ExcelProperty(value = "最大限额")
    private BigDecimal maxAmount;

    /**
     * 最小限额
     */
    @ExcelProperty(value = "最小限额")
    private BigDecimal minAmount;

    /**
     * 固定金额
     */
    @ExcelProperty(value = "固定金额")
    private BigDecimal fixedAmount;


}
