package com.ruoyi.boss.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 通道列表业务对象
 *
 * @author ruoyi
 * @date 2023-11-24
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class PayChannelBo extends BaseEntity {

    /**
     *
     */

    private Long id;

    /**
     * 通道名称
     */
    @NotBlank(message = "通道名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 最大金额
     */
    @NotNull(message = "最大金额不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal maxAmount;

    /**
     * 最小金额
     */
    @NotNull(message = "最小金额不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal minAmount;

    /**
     * 通道编码
     */
    @NotBlank(message = "通道编码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String code;

    /**
     * 状态0 开启 1关闭
     */
    private Integer status;

    /**
     * 通道汇率
     */

    private BigDecimal rate;

    /**
     * 通道返点
     */

    private BigDecimal refund;

    /**
     * 备注
     */
    private String remark;


}
