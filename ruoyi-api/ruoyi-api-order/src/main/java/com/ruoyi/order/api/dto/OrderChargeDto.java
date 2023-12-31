package com.ruoyi.order.api.dto;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商户充值订单业务对象
 *
 * @author ruoyi
 * @date 2023-11-22
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderChargeDto extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 充值金额
     */
    @NotNull(message = "充值金额不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal amount;


    private Integer status;


    /**
     * 充值汇率
     */
    @NotNull(message = "充值汇率不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal rate;

    /**
     * 商户结算汇率
     */

    private BigDecimal merchantRate;

    /**
     * 代理结算汇率
     */

    private BigDecimal agentRate;

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

    private Long tenantId;

    private String tenantName;

    private List<Long> tenantIds;

    private String parentName;
    private Long parentId;

    private Integer isFloat;
}
