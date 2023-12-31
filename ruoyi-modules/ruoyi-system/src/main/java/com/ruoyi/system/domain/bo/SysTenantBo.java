package com.ruoyi.system.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 租户业务对象
 *
 * @author ruoyi
 * @date 2023-11-20
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class SysTenantBo extends BaseEntity {

    /**
     * id
     */

    private Long id;

    /**
     * 租户编号
     */
    private String name;

    /**
     * 联系人
     */
    private String contactUserName;

    /**
     * 联系电话
     */

    private String contactPhone;



    /**
     * 备注
     */
    private String remark;


    /**
     * 过期时间
     */

    private Date expireTime;

    private Long accountCount;

    /**
     * 租户状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 创建部门
     */
    private Long createDept;
    @NotBlank(message = "登录名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String loginUser;
    @NotBlank(message = "密码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String loginPassword;
    @NotNull(message = "租户类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer tenantType;

    private Integer version;

    private Boolean googleCaptcha;
    private BigDecimal balance;
    private String secret;
    private BigDecimal payforFee;
    private BigDecimal payforRate;
    private BigDecimal chargeRate;
    private String code;

    private Integer autoPay;
    private BigDecimal floatDown;
    private BigDecimal floatUp;
    private String currency;
    private String telegram;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String apiWhiteList;
    private String whiteList;
    private Integer openApiWhiteList;
    private Integer openWhiteList;
    private List<Long> checkedList;

    /**
     * 渠道域名
     */
    private String channelDomain;
    /**
     * 商户域名
     */
    private String merchantDomain;

    /**
     * 充值地址
     */
    private String chargeAddress;

    private Integer isFloat;

}
