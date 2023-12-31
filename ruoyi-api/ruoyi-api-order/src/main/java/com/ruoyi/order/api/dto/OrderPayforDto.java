package com.ruoyi.order.api.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.core.dto.PayOrderChannelDto;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 代付订单业务对象
 *
 * @author ruoyi
 * @date 2023-11-22
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderPayforDto extends BaseEntity {

    /**
     *
     */
    @ExcelIgnore
    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 商户ID
     */
    @NotNull(message = "商户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    @ExcelIgnore
    private Long tenantId;

    /**
     * 商户名称
     */
    @NotBlank(message = "商户名称不能为空", groups = { AddGroup.class, EditGroup.class })
    @ExcelProperty(value = "商户名称")
    private String tenantName;


    @ExcelProperty(value = "商户编号")
    private String tenantCode;

    /**
     * 系统订单号
     */
    @NotNull(message = "系统订单号不能为空", groups = { AddGroup.class, EditGroup.class })
    @ExcelProperty(value = "订单号")
    private String orderNo;

    /**
     * 商户订单号
     */
    @NotNull(message = "商户订单号不能为空", groups = { AddGroup.class, EditGroup.class })
    @ExcelProperty(value = "商户订单号")
    private String appOrderNo;



    /**
     * 商户手续费
     */
    @NotNull(message = "商户手续费不能为空", groups = { AddGroup.class, EditGroup.class })
    @ExcelProperty(value = "订单手续费")
    private BigDecimal payforFee;

    @NotNull(message = "商户手续费不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "订单手续费率")
    private BigDecimal payforRate;


    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空", groups = { AddGroup.class, EditGroup.class })
    @ExcelProperty(value = "用户名")
    private String userName;

    /**
     * 银行卡号
     */
    @NotBlank(message = "银行卡号不能为空", groups = { AddGroup.class, EditGroup.class })
    @ExcelProperty(value = "银行卡号")
    private String bankAccount;

    /**
     * 银行名称
     */
    @NotBlank(message = "银行名称不能为空", groups = { AddGroup.class, EditGroup.class })
    @ExcelProperty(value = "开户行")
    private String bankName;

    /**
     * 状态0 待处理 1已发送 2 回调成功 3回调失败
     */
    @ExcelProperty(value = "订单状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_order_status")
    private Integer status;

    /**
     * 金额
     */
    @NotNull(message = "金额不能为空", groups = { AddGroup.class, EditGroup.class })
    @ExcelProperty(value = "金额")
    private BigDecimal amount;

    /**
     * 回调时间
     */
    @NotNull(message = "回调时间不能为空", groups = { AddGroup.class, EditGroup.class })
    @ExcelProperty(value = "回调时间")
    private Date backTime;

    @ExcelProperty(value = "通道名称")
    private String wayName;
    @ExcelProperty(value = "通道编码")
    private String wayCode;
    @ExcelProperty(value = "通道手续费")
    private BigDecimal wayFee;
    @ExcelProperty(value = "通道费率")
    private BigDecimal wayRate;
    @ExcelProperty(value = "通道返回编码")
    private String errorCode;
    @ExcelProperty(value = "通道返回消息")
    private String errorMessage;

    @ExcelProperty(value = "备注")
    private String remark;
    @ExcelIgnore
    private List<Long> tenantIds;

    @ExcelIgnore
    private List<PayOrderChannelDto> channelList;

}
