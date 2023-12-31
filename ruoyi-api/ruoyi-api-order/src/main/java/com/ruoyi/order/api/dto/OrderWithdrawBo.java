package com.ruoyi.order.api.dto;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 提现订单业务对象
 *
 * @author ruoyi
 * @date 2023-11-22
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderWithdrawBo extends BaseEntity {

    /**
     *
     */

    private Long id;

    /**
     *
     */

    private Long tenantId;

    /**
     *
     */

    private String tenantName;

    /**
     * 订单编号
     */

    private String orderNo;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String userName;

    /**
     * 提现类型
     */

    private String withdrawType;

    /**
     * 户名
     */
    @NotBlank(message = "户名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String userAccount;

    /**
     * 卡号/地址
     */
    @NotBlank(message = "卡号/地址不能为空", groups = { AddGroup.class, EditGroup.class })
    private String userAccountNo;

    /**
     * 提现金额
     */
    @NotNull(message = "提现金额不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal amount;

    /**
     * 结算汇率
     */
    @NotNull(message = "结算汇率不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal rate;

    /**
     * 结算金额
     */
    @NotNull(message = "结算金额不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal realAmount;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    private List<Long> tenantIds;
}
