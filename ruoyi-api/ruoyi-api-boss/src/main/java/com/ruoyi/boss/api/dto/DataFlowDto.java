package com.ruoyi.boss.api.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.core.dto.PayOrderDto;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.common.core.enums.DataFlowType;
import com.ruoyi.common.core.utils.SeqKit;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 资金明细业务对象
 *
 * @author ruoyi
 * @date 2023-11-26
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DataFlowDto extends BaseTenantEntity {

    /**
     * 序列号
     */
    @NotNull(message = "序列号不能为空", groups = {EditGroup.class})
    @ExcelProperty(value = "序列号")
    private Long id;

    /**
     * 总资产
     */
    @NotNull(message = "总资产不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "总资产")
    private BigDecimal userAmount;

    /**
     * 用户号
     */
    @NotBlank(message = "用户号不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "用户号")
    private String userAccount;

    /**
     * 当前余额
     */
    @NotNull(message = "当前余额不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "当前余额")
    private BigDecimal currencyAmount;

    /**
     * 原余额
     */
    @NotNull(message = "原余额不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "原余额")
    private BigDecimal beforeAmount;

    /**
     * 操作金额
     */
    @NotNull(message = "操作金额不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "操作金额")
    private BigDecimal amount;

    /**
     * 当前冻结
     */
    @NotNull(message = "当前冻结不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "当前冻结金额")
    private BigDecimal beforeFreeAmount;

    /**
     * 原冻结
     */
    @NotNull(message = "原冻结不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "原冻结金额")
    private BigDecimal freeAmount;

    /**
     * 手续费
     */
    @NotNull(message = "手续费不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "手续费")
    private BigDecimal fee;

    /**
     * 交易流水号
     */
    @NotBlank(message = "交易流水号不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "流水号")
    private String orderNo;

    /**
     * 0 充值 1支付 2提现 3代付 4 手续费
     */
    @NotNull(message = "流水类型", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "流水类型")
    private Integer flowType;

    /**
     * 商户ID
     */
    @NotNull(message = "商户ID不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelIgnore()
    private Long tenantId;

    /**
     * 商户号
     */
    @NotBlank(message = "商户号不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "商户号")
    private String tenantName;

    @ExcelProperty(value = "操作时间")
    private Date createTime;

    private String remark;

    private List<Long> tenantIds;

    /**
     * 构建充值订单
     *
     * @param tenant
     * @param amount
     * @return
     */
    public static List<DataFlowDto> buildCharge(TenantDto tenant, BigDecimal amount) {
        DataFlowDto dataFlowDto = new DataFlowDto();
        dataFlowDto.setOrderNo(SeqKit.genFlowOrderId());
        dataFlowDto.setTenantName(tenant.getName());
        dataFlowDto.setTenantId(tenant.getId());
        dataFlowDto.setAmount(amount);
        dataFlowDto.setFee(BigDecimal.ZERO);
        dataFlowDto.setBeforeFreeAmount(tenant.getFreezeBalance());
        dataFlowDto.setBeforeAmount(tenant.getBalance());
        dataFlowDto.setCurrencyAmount(tenant.getBalance().add(amount));
        dataFlowDto.setUserAccount(tenant.getName());
        dataFlowDto.setUserAmount(tenant.getBalance());
        dataFlowDto.setFlowType(DataFlowType.RECHARGE.getCode());
        dataFlowDto.setRemark("充值:" + amount);
        dataFlowDto.setCreateTime(new Date());
        return Arrays.asList(dataFlowDto);
    }

    /**
     * 构建提现流水
     *
     * @param tenant
     * @param amount
     * @return
     */
    public static List<DataFlowDto> buildWithdraw(TenantDto tenant, BigDecimal amount) {
        DataFlowDto dataFlowDto = new DataFlowDto();
        dataFlowDto.setOrderNo(SeqKit.genFlowOrderId());
        dataFlowDto.setTenantName(tenant.getName());
        dataFlowDto.setTenantId(tenant.getId());
        dataFlowDto.setAmount(amount);
        dataFlowDto.setFee(BigDecimal.ZERO);
        dataFlowDto.setBeforeFreeAmount(tenant.getFreezeBalance());
        dataFlowDto.setBeforeAmount(tenant.getBalance());
        dataFlowDto.setCurrencyAmount(tenant.getBalance().subtract(amount));
        dataFlowDto.setUserAccount(tenant.getName());
        dataFlowDto.setUserAmount(tenant.getBalance());
        dataFlowDto.setFlowType(DataFlowType.WITHDRAW.getCode());
        dataFlowDto.setRemark("提现金额:" + amount);
        dataFlowDto.setCreateTime(new Date());

        return Arrays.asList(dataFlowDto);
    }

    /**
     * 构建代付流水
     *
     * @param tenant
     * @param orderDto
     * @return
     */
    public static List<DataFlowDto> buildPayfor(TenantDto tenant, PayOrderDto orderDto) {
        Date now = new Date();

        DataFlowDto dataFlowDto = new DataFlowDto();
        dataFlowDto.setOrderNo(SeqKit.genFlowOrderId());
        dataFlowDto.setTenantName(tenant.getName());
        dataFlowDto.setTenantId(tenant.getId());
        dataFlowDto.setAmount(orderDto.getValidAmount());
        dataFlowDto.setFee(tenant.getPayforFee());
        dataFlowDto.setBeforeFreeAmount(tenant.getFreezeBalance());
        dataFlowDto.setFreeAmount(tenant.getFreezeBalance().subtract(dataFlowDto.getAmount()));
        dataFlowDto.setBeforeAmount(tenant.getBalance());
        dataFlowDto.setCurrencyAmount(tenant.getBalance().subtract(dataFlowDto.getAmount()));
        dataFlowDto.setUserAccount(tenant.getName());
        dataFlowDto.setUserAmount(tenant.getBalance());
        dataFlowDto.setFlowType(DataFlowType.PAYFOR.getCode());
        dataFlowDto.setRemark("代付订单扣除:" + dataFlowDto.getAmount() + "," +
                "释放冻结金额:" + orderDto.getValidAmount() + "," +
                "手续费:" + orderDto.getPayforFee() + "," +
                "通道手续费:" + orderDto.getWayFee()
        );
        dataFlowDto.setCreateTime(now);
        return Arrays.asList(dataFlowDto);
    }

    public static List<DataFlowDto> buildFreeBalance(TenantDto tenant, BigDecimal freeAmount) {
        Date now = new Date();
        DataFlowDto dataFlowDto = new DataFlowDto();
        dataFlowDto.setOrderNo(SeqKit.genFlowOrderId());
        dataFlowDto.setTenantName(tenant.getName());
        dataFlowDto.setTenantId(tenant.getId());
        dataFlowDto.setAmount(freeAmount);
        dataFlowDto.setFee(tenant.getPayforFee());
        dataFlowDto.setBeforeFreeAmount(tenant.getFreezeBalance());
        dataFlowDto.setFreeAmount(tenant.getFreezeBalance().add(dataFlowDto.getAmount()));
        dataFlowDto.setBeforeAmount(tenant.getBalance());
        dataFlowDto.setCurrencyAmount(tenant.getBalance());
        dataFlowDto.setUserAccount(tenant.getName());
        dataFlowDto.setUserAmount(tenant.getBalance());
        dataFlowDto.setFlowType(DataFlowType.PAYFOR.getCode());
        dataFlowDto.setRemark("代付冻结金额:" + dataFlowDto.getAmount());
        dataFlowDto.setCreateTime(now);
        return Arrays.asList(dataFlowDto);
    }


}
