package com.ruoyi.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 提现订单对象 order_withdraw
 *
 * @author ruoyi
 * @date 2023-11-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_withdraw")
public class OrderWithdraw extends BaseTenantEntity {

    private static final long serialVersionUID=1L;

    /**
     *
     */
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 提现类型
     */
    private String withdrawType;
    /**
     * 户名
     */
    private String userAccount;
    /**
     * 卡号/地址
     */
    private String userAccountNo;
    /**
     * 提现金额
     */
    private BigDecimal amount;
    /**
     * 结算汇率
     */
    private BigDecimal rate;
    /**
     * 结算金额
     */
    private BigDecimal realAmount;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

    private String parentName;

    private Long parentId;


}
