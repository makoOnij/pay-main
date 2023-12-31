package com.ruoyi.system.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 租户业务对象
 *
 * @author ruoyi
 * @date 2023-11-20
 */

@Data
public class BatchTenantDto implements Serializable {

    /**
     * id
     */

    @NotBlank(message = "请选择商户ID")
    private List<Long> ids;


    /**
     * 租户状态（0正常 1停用）
     */
    private Integer status;


    private String whiteList;

    private String apiWhiteList;

    private BigDecimal chargeRate;

    private BigDecimal floatUp;
    private BigDecimal floatDown;


}
