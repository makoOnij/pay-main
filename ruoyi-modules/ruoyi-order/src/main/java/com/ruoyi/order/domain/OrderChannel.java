package com.ruoyi.order.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import java.math.BigDecimal;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 渠道充值订单对象 order_channel
 *
 * @author ruoyi
 * @date 2023-11-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_channel")
public class OrderChannel extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 主键，自增
     */
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 渠道名称
     */
    private String channelName;
    /**
     * 状态0 正常 1成功 2失败
     */
    private Integer status;
    /**
     * 充值金额
     */
    private BigDecimal amount;
    /**
     * 汇率
     */
    private BigDecimal rate;
    /**
     * 结算汇率
     */
    private BigDecimal settleRate;
    /**
     * 到账金额
     */
    private BigDecimal realAmount;
    /**
     * 备注
     */
    private String remark;
    /**
     *
     */
    private Long tenantId;
    /**
     * 商户名称
     */
    private String tenantName;

}
