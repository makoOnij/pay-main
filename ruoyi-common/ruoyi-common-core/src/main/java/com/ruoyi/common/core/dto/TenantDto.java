package com.ruoyi.common.core.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 租户对象 sys_tenant
 *
 * @author ruoyi
 * @date 2023-11-20
 */
@Data
public class TenantDto extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 租户编号
     */
    private String name;
    private String code;

    private String tenantName;
    private Long tenantId;
    /**
     * 联系人
     */
    private String contactUserName;
    /**
     * 联系电话
     */
    private String contactPhone;
    /**
     * 商户余额
     */
    private BigDecimal balance;

    private BigDecimal freezeBalance;

    /**
     * 可用余额
     *
     * @return
     */
    public BigDecimal getValidBalance() {
        return balance.subtract(freezeBalance);
    }

    private BigDecimal validBalance;

    /**
     * 备注
     */
    private String remark;
    /**
     * 过期时间
     */
    private Date expireTime;
    /**
     * 用户数量（-1不限制）
     */
    private Long accountCount;
    /**
     * 租户状态（0正常 1停用）
     */
    private Integer status;

    private String loginUser;
    private String loginPassword;
    private Integer tenantType;
    @Version
    private Integer version;

    /**
     * 是否开启谷歌验证码
     */
    private Boolean googleCaptcha;

    private String secret;

    private String whiteList;

    private BigDecimal payforFee;
    private BigDecimal payforRate;
    private BigDecimal chargeRate;

    private Integer autoPay;
    private BigDecimal floatDown;
    private BigDecimal floatUp;
    private BigDecimal floatAmount;
    private String currency;
    private String telegram;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String apiWhiteList;
    private Integer openWhiteList;
    private Integer openApiWhiteList;

    private Integer isFloat;

    /**
     * 渠道域名
     */
    private String channelDomain;
    /**
     * 商户域名
     */
    private String merchantDomain;

}
