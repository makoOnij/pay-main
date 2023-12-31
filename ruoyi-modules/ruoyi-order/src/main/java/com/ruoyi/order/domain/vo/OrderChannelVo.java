package com.ruoyi.order.domain.vo;

import java.math.BigDecimal;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;
import java.util.Date;
import java.io.Serializable;


/**
 * 渠道充值订单视图对象
 *
 * @author ruoyi
 * @date 2023-11-22
 */
@Data
@ExcelIgnoreUnannotated
public class OrderChannelVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，自增
     */
    @ExcelProperty(value = "主键，自增")
    private Long id;

    /**
     * 渠道名称
     */
    @ExcelProperty(value = "渠道名称")
    private String channelName;

    /**
     * 状态0 正常 1成功 2失败
     */
    @ExcelProperty(value = "状态0 正常 1成功 2失败")
    private Integer status;

    /**
     * 充值金额
     */
    @ExcelProperty(value = "充值金额")
    private BigDecimal amount;

    /**
     * 汇率
     */
    @ExcelProperty(value = "汇率")
    private BigDecimal rate;

    /**
     * 结算汇率
     */
    @ExcelProperty(value = "结算汇率")
    private BigDecimal settleRate;

    /**
     * 到账金额
     */
    @ExcelProperty(value = "到账金额")
    private BigDecimal realAmount;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
