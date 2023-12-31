package com.ruoyi.order.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 提现订单视图对象
 *
 * @author ruoyi
 * @date 2023-11-22
 */
@Data
@ExcelIgnoreUnannotated
public class OrderWithdrawVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long id;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long tenantId;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String tenantName;

    /**
     * 订单编号
     */
    @ExcelProperty(value = "订单编号")
    private String orderNo;

    /**
     * 用户名
     */
    @ExcelProperty(value = "用户名")
    private String userName;

    /**
     * 提现类型
     */
    @ExcelProperty(value = "提现类型")
    private String withdrawType;

    /**
     * 户名
     */
    @ExcelProperty(value = "户名")
    private String userAccount;

    /**
     * 卡号/地址
     */
    @ExcelProperty(value = "卡号/地址")
    private String userAccountNo;

    /**
     * 提现金额
     */
    @ExcelProperty(value = "提现金额")
    private BigDecimal amount;

    /**
     * 结算汇率
     */
    @ExcelProperty(value = "结算汇率")
    private BigDecimal rate;

    /**
     * 结算金额
     */
    @ExcelProperty(value = "结算金额")
    private BigDecimal realAmount;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态")
    private Integer status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    private String parentName;

    private Date createTime;


}
