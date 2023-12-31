package com.ruoyi.system.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 租户业务对象
 *
 * @author ruoyi
 * @date 2023-11-20
 */

@Data
public class UpdateTenantSecurityDto implements Serializable {

    /**
     * id
     */

    private Long id;

    private String openWhiteList;

    private String openApiWhiteList;

    private String googleSecret;

    private Integer autoPay;

    private String whiteList;
    private String apiWhiteList;


}
