package com.ruoyi.boss.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 通道列表视图对象
 *
 * @author ruoyi
 * @date 2023-11-24
 */
@Data
@ExcelIgnoreUnannotated
public class PayChannelVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    @ExcelProperty(value = "")
    private Long id;

    /**
     * 通道名称
     */
    @ExcelProperty(value = "通道名称")
    private String name;

    /**
     * 最大金额
     */
    @ExcelProperty(value = "最大金额")
    private BigDecimal maxAmount;

    /**
     * 最小金额
     */
    @ExcelProperty(value = "最小金额")
    private BigDecimal minAmount;

    /**
     * 通道编码
     */
    @ExcelProperty(value = "通道编码")
    private String code;

    /**
     * 状态0 开启 1关闭
     */
    @ExcelProperty(value = "状态0 开启 1关闭")
    private Integer status;

    /**
     * 通道汇率
     */
    @ExcelProperty(value = "通道汇率")
    private BigDecimal rate;

    /**
     * 通道返点
     */
    @ExcelProperty(value = "通道返点")
    private BigDecimal refund;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
