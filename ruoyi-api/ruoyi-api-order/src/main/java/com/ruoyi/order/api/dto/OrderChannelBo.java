package com.ruoyi.order.api.dto;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 渠道充值订单业务对象
 *
 * @author ruoyi
 * @date 2023-11-22
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderChannelBo extends BaseEntity {

    /**
     * 主键，自增
     */
    @NotNull(message = "主键，自增不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 渠道名称
     */
    @NotBlank(message = "渠道名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String channelName;

    /**
     * 状态0 正常 1成功 2失败
     */
    @NotNull(message = "状态0 正常 1成功 2失败不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer status;

    /**
     * 充值金额
     */
    @NotNull(message = "充值金额不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal amount;

    /**
     * 汇率
     */
    @NotNull(message = "汇率不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal rate;

    /**
     * 结算汇率
     */
    @NotNull(message = "结算汇率不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal settleRate;

    /**
     * 到账金额
     */
    @NotNull(message = "到账金额不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal realAmount;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
