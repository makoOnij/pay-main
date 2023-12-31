package com.ruoyi.common.core.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 商户通知业务对象
 *
 * @author ruoyi
 * @date 2023-11-27
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class TenantNotifyDto extends BaseEntity {
    //订单类型:1-支付,2-退款, 3-转账
    public static final int TYPE_PAY_ORDER = 1;
    public static final int TYPE_REFUND_ORDER = 2;
    public static final int TYPE_TRANSFER_ORDER = 3;

    //通知状态
    public static final int STATE_ING = 1;
    public static final int STATE_SUCCESS = 2;
    public static final int STATE_FAIL = 3;
    public static final int STATE_NON = 4;
    /**
     * 序列号
     */
    @ExcelProperty(value = "序列号")
    private Long notifyId;

    /**
     * 订单ID
     */
    @ExcelProperty(value = "订单ID")
    private Long orderId;

    /**
     * 1 支付 2退款
     */
    @ExcelProperty(value = "通知类型")
    private Integer orderType;

    /**
     * 商户订单号
     */
    @ExcelProperty(value = "商户订单号")
    private String mchOrderId;

    /**
     * 商户号
     */
    @ExcelProperty(value = "商户号")
    private String tenantName;

    /**
     * 商户ID
     */
    @ExcelProperty(value = "商户ID")
    private Long tenantId;

    /**
     * 通知地址
     */
    @ExcelProperty(value = "通知地址")
    private String notifyUrl;

    /**
     * 响应结果
     */
    @ExcelProperty(value = "响应结果")
    private String resResult;

    /**
     * 通知次数
     */

    @ExcelProperty(value = "通知次数")
    private Long notifyCount;

    /**
     * 1 通知中 2通知成功 3通知失败
     */

    @ExcelProperty(value = "通知状态")
    private Integer state;

    /**
     * 最后通知时间
     */

    @ExcelProperty(value = "最后通知时间")
    private Date lastNotifyTime;


    private Integer notifyCountLimit;

}
