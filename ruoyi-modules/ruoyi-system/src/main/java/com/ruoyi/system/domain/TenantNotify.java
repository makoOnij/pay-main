package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 商户通知对象 tenant_notify
 *
 * @author ruoyi
 * @date 2023-11-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant_notify")
public class TenantNotify extends BaseTenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 序列号
     */
    @TableId(value = "notify_id")
    private Long notifyId;
    /**
     * 订单ID
     */
    private Long orderId;
    /**
     * 1 支付 2退款
     */
    private Integer orderType;
    /**
     * 商户订单号
     */
    private String mchOrderId;

    /**
     * 通知地址
     */
    private String notifyUrl;
    /**
     * 响应结果
     */
    private String resResult;
    /**
     * 通知次数
     */
    private Long notifyCount;
    /**
     * 1 通知中 2通知成功 3通知失败
     */
    private Integer state;
    /**
     * 最后通知时间
     */
    private Date lastNotifyTime;

    private Integer notifyCountLimit;

}
