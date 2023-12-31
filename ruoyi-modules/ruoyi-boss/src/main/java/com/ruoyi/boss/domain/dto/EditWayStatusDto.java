package com.ruoyi.boss.domain.dto;

import com.ruoyi.boss.domain.TenantChannel;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 通道列表业务对象
 *
 * @author ruoyi
 * @date 2023-11-24
 */

@Data
public class EditWayStatusDto implements Serializable {

    /**
     *
     */
    @NotNull(message = "操作对象不能为空", groups = {AddGroup.class, EditGroup.class})
    private List<Long> ids;


    /**
     * 状态0 开启 1关闭
     */
    @NotNull(message = "商户通道不能为空", groups = {AddGroup.class, EditGroup.class})
    private TenantChannel merchantWay;
}
