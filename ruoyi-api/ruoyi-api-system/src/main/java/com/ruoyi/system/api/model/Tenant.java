package com.ruoyi.system.api.model;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户信息
 *
 * @author ruoyi
 */
@Data
@NoArgsConstructor
public class Tenant implements Serializable {
    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 租户编号
     */
    private String tenantName;

    private Boolean googleCaptcha;

    private Integer status;


}
