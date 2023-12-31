package com.ruoyi.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商户充值订单对象 order_merchant
 *
 * @author ruoyi
 * @date 2023-11-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_charge")
public class OrderCharge extends BaseTenantEntity {

    private static final long serialVersionUID=1L;

    /**
     *
     */
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 充值金额
     */
    private BigDecimal amount;
    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 充值汇率
     */
    private BigDecimal rate;

    /**
     * 到账金额
     */
    private BigDecimal realAmount;
    /**
     * 充值地址
     */
    private String chargeAddress;
    /**
     * 备注
     */
    private String remark;

    private Integer isFloat;

    private String parentName;

    private Long parentId;


}
