package com.ruoyi.auth.domain;

import lombok.Data;

/**
 * 登录租户对象
 *
 * @author Michelle.Chung
 */
@Data
public class LoginTenantVo {

    /**
     * 租户开关
     */
    private Boolean tenantEnabled;

    /**
     * 租户对象列表
     */
    private TenantVo data;

}
