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
 * 代付通道业务对象
 *
 * @author ruoyi
 * @date 2023-11-24
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class TenantChannelBo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Long id;



    /**
     * 通道ID
     */

    private Long payId;


    /**
     *
     */

    private String payName;

    /**
     *
     */

    private String payCode;

    /**
     *
     */

    private BigDecimal rate;

    /**
     *
     */

    private BigDecimal fee;

    /**
     * 代付名称
     */
    @NotBlank(message = "代付名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 权重
     */
    @NotNull(message = "权重不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long sort;

    /**
     * 币种
     */
    private String currency;

    /**
     * 代付方式
     */
    private Integer wayType;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 最大限额
     */

    private BigDecimal maxAmount;

    /**
     * 最小限额
     */

    private BigDecimal minAmount;

    /**
     * 固定金额
     */

    private BigDecimal fixedAmount;

    private Long tenantId;

    private String tenantName;


}
