package com.ruoyi.order.api.dto;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 提现订单业务对象
 *
 * @author ruoyi
 * @date 2023-11-22
 */

@Data
public class AuditWithdrawDto implements Serializable {

    /**
     *
     */
    @NotNull(message = "提现订单信息错误", groups = {AddGroup.class, EditGroup.class})
    private Long id;


    /**
     * 状态
     */
    @NotNull(message = "请选择审核状态", groups = {AddGroup.class, EditGroup.class})
    private Integer status;

    /**
     * 备注
     */
    private String remark;


}
