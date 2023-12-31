package com.ruoyi.system.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 租户业务对象
 *
 * @author ruoyi
 * @date 2023-11-20
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class SysTenantChargeDto extends BaseEntity {

    /**
     * id
     */

    private Long id;

    /**
     * 租户编号
     */
    private String tenantName;

    /**
     * 备注
     */
    private String remark;


    private Integer version;
    private BigDecimal balance;
    @NotNull(message = "充值金额不能为空", groups = {AddGroup.class, EditGroup.class})
    private BigDecimal amount;

}
