package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 租户对象 sys_tenant
 *
 * @author ruoyi
 * @date 2023-11-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant")
public class SysTenant extends BaseTenantEntity {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 租户名称
     */
    private String name;
    /**
     * 租户编号
     */
    private String code;

    /**
     * 商户余额
     */
    private BigDecimal balance;
    /**
     * 冻结金额
     */
    private BigDecimal freezeBalance;

    /**
     * 备注
     */
    private String remark;
    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 租户状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 登录账号
     */
    private String loginUser;
    /**
     * 登录密码
     */
    private String loginPassword;
    /**
     * 租户类型
     */
    private Integer tenantType;

    /**
     * 是否开启谷歌验证码
     */
    private Boolean googleCaptcha;
    /**
     * 商户私钥
     */
    private String secret;

    /**
     * 代付费用
     */
    private BigDecimal payforFee;
    /**
     * 代付费率
     */
    private BigDecimal payforRate;
    /**
     * 充值费率
     */
    private BigDecimal chargeRate;

    /**
     * 是否自动代付
     */
    private Integer autoPay;
    private BigDecimal floatDown;

    private BigDecimal floatUp;
    /**
     * 货币类型
     */
    private String currency;
    /**
     * 货币汇率
     */
    private BigDecimal currencyRate;
    /**
     * 纸飞机
     */
    private String telegram;
    /**
     * 最小金额
     */
    private BigDecimal minAmount;
    /**
     * 最大金额
     */
    private BigDecimal maxAmount;
    /**
     * 登录白名单
     */
    private String whiteList;
    /**
     * api白名单
     */
    private String apiWhiteList;
    /**
     * 是否开启api白名单
     */
    private Integer openApiWhiteList;
    /**
     * 是否开启白名单
     */
    private Integer openWhiteList;

    /**
     * 联系人
     */
    private String contactUserName;

    /**
     * 联系电话
     */

    private String contactPhone;

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
