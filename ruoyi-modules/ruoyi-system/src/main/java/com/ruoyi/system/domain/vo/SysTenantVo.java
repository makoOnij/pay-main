package com.ruoyi.system.domain.vo;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.boss.api.domain.TenantWayDto;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import com.ruoyi.system.api.domain.SysUser;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 租户视图对象
 *
 * @author ruoyi
 * @date 2023-11-20
 */
@Data
@ExcelIgnoreUnannotated
public class SysTenantVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ExcelProperty(value = "id")
    private Long id;

    /**
     * 租户编号
     */
    @ExcelProperty(value = "租户编号")
    private String name;


    @ExcelProperty(value = "商户号")
    private String code;

    /**
     * 联系人
     */
    @ExcelProperty(value = "联系人")
    private String contactUserName;

    /**
     * 联系电话
     */
    @ExcelProperty(value = "联系电话")
    private String contactPhone;

    /**
     * 企业名称
     */
    @ExcelProperty(value = "企业名称")
    private String companyName;

    /**
     * 统一社会信用代码
     */
    @ExcelProperty(value = "统一社会信用代码")
    private String licenseNumber;

    /**
     * 地址
     */
    @ExcelProperty(value = "地址")
    private String address;

    /**
     * 企业简介
     */
    @ExcelProperty(value = "企业简介")
    private String intro;

    /**
     * 域名
     */
    @ExcelProperty(value = "域名")
    private String domain;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 租户套餐编号
     */
    @ExcelProperty(value = "租户套餐编号")
    private Long packageId;

    /**
     * 过期时间
     */
    @ExcelProperty(value = "过期时间")
    private Date expireTime;

    /**
     * 用户数量（-1不限制）
     */
    @ExcelProperty(value = "用户数量", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "-=1不限制")
    private Long accountCount;

    /**
     * 租户状态（0正常 1停用）
     */
    @ExcelProperty(value = "租户状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=正常,1=停用")
    private Integer status;

    /**
     * 创建部门
     */
    @ExcelProperty(value = "创建部门")
    private Long createDept;
    private Integer tenantType;
    private String loginUser;
    private String loginPassword;
    private Integer version;
    private BigDecimal balance;
    private BigDecimal freezeBalance;

    private Boolean googleCaptcha;

    private String secret;

    private String whiteList;
    private String apiWhiteList;
    private BigDecimal payforFee;
    private BigDecimal payforRate;
    private BigDecimal chargeRate;
    private String tenantCode;

    private Integer autoPay;
    private BigDecimal floatDown;
    private BigDecimal floatUp;
    private BigDecimal floatAmount;
    private String currency;
    private BigDecimal currencyRate;
    private String telegram;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Integer openApiWhiteList;
    private Integer openWhiteList;

    private SysUser user;

    private List<TenantWayDto> tenantWays;

    private Date createTime;

    public List<Long> getCheckedList() {
        if (ObjectUtil.isEmpty(tenantWays)) {
            return new ArrayList<>();
        }
        return tenantWays.stream().map(TenantWayDto::getPayId).collect(Collectors.toList());
    }

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
