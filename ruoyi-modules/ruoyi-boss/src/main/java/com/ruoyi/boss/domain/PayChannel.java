package com.ruoyi.boss.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 通道列表对象 pay_way
 *
 * @author ruoyi
 * @date 2023-11-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pay_way")
public class PayChannel extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 通道名称
     */
    private String name;
    /**
     * 最大金额
     */
    private BigDecimal maxAmount;
    /**
     * 最小金额
     */
    private BigDecimal minAmount;
    /**
     * 通道编码
     */
    private String code;
    /**
     * 状态0 开启 1关闭
     */
    private Integer status;
    /**
     * 通道汇率
     */
    private BigDecimal rate;
    /**
     * 通道返点
     */
    private BigDecimal refund;
    /**
     * 备注
     */
    private String remark;

    private String mchId;
    private String mchKey;
    private String mchDomain;

}
