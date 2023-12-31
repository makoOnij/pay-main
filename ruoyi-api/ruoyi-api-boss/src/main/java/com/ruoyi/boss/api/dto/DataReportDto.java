package com.ruoyi.boss.api.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.common.core.dto.PayOrderDto;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 财务报业务对象
 *
 * @author ruoyi
 * @date 2023-11-26
 */

@Data
public class DataReportDto implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ExcelProperty(value = "商户号")
    private String tenantName;

    /**
     * 商户ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ExcelIgnore
    private Long tenantId;

    /**
     * 序号
     */
    @NotNull(message = "序号不能为空", groups = {EditGroup.class})
    @ExcelProperty(value = "序列号")
    private Long id;

    /**
     * 创建日期
     */
    private LocalDate createTime;

    /**
     * 总资产
     */
    @NotNull(message = "总资产不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "总资产")
    private BigDecimal totalAmount;

    /**
     * 余额
     */
    @NotNull(message = "余额不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "余额")
    private BigDecimal totalBalance;

    /**
     * 冻结
     */
    @NotNull(message = "冻结不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "冻结金额")
    private BigDecimal totalFreeze;

    /**
     * 充值
     */
    @NotNull(message = "充值不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "充值金额")
    private BigDecimal totalCharge;

    /**
     * 提现
     */
    @NotNull(message = "提现不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "提现金额")
    private BigDecimal totalWithdraw;

    /**
     * 代付
     */
    @NotNull(message = "代付不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "代付金额")
    private BigDecimal totalPayfor;

    @NotNull(message = "代付不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "代付笔数")
    private Long totalPayforCount;

    /**
     * 订单数
     */
    @NotNull(message = "订单数不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "订单数")
    private Long totalOrderCount;


    @ExcelProperty(value = "订单金额")
    private BigDecimal totalOrderAmount;

    /**
     * 手续费
     */
    @NotNull(message = "手续费不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "手续费")
    private BigDecimal totalFee;

    /**
     * 代付费率%
     */
    @NotNull(message = "代付费率%不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "代付费率")
    private BigDecimal totalPayforRate;

    /**
     * 代付附加费
     */
    @NotNull(message = "代付附加费不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "代付附加费")
    private BigDecimal totalPayforFee;


    private List<Long> ids;



    /**
     * 构建充值报表数据
     *
     * @param tenant
     * @param amount
     * @param fee
     * @return
     */
    public static DataReportDto buildCharge(TenantDto tenant, BigDecimal amount, BigDecimal fee) {

        DataReportDto reportDto = new DataReportDto();
        reportDto.setCreateTime(LocalDate.now());
        reportDto.setTenantName(tenant.getName());
        reportDto.setTenantId(tenant.getId());
        reportDto.setTotalCharge(amount);
        reportDto.setTotalBalance(amount);
        reportDto.setTotalFee(fee);
        reportDto.setTotalOrderCount(1L);
        return reportDto;

    }

    /**
     * 构建提现报表数据
     *
     * @param tenant
     * @param amount
     * @return
     */
    public static DataReportDto buildWithdraw(TenantDto tenant, BigDecimal amount) {

        DataReportDto reportDto = new DataReportDto();
        reportDto.setCreateTime(LocalDate.now());
        reportDto.setTenantName(tenant.getName());
        reportDto.setTenantId(tenant.getId());
        reportDto.setTotalOrderCount(1L);
        reportDto.setTotalWithdraw(amount);
        return reportDto;

    }

    /**
     * 构建代付报表数据
     *
     * @param tenant
     * @return
     */
    public static DataReportDto buildPayfor(TenantDto tenant, PayOrderDto orderDto) {
        DataReportDto reportDto = new DataReportDto();
        reportDto.setCreateTime(LocalDate.now());
        reportDto.setTenantName(tenant.getName());
        reportDto.setTenantId(tenant.getId());
        reportDto.setTotalFee(orderDto.getPayforFee().add(orderDto.getWayFee()));
        reportDto.setTotalOrderCount(1L);
        reportDto.setTotalPayfor(orderDto.getAmount());
        reportDto.setTotalPayforFee(tenant.getPayforFee());

        return reportDto;
    }

    /**
     * 构建空报表数据
     *
     * @return
     */
    public static DataReportDto buildEmpty() {
        DataReportDto reportDto = new DataReportDto();
        reportDto.setCreateTime(LocalDate.now());
        reportDto.setTotalCharge(BigDecimal.ZERO);
        reportDto.setTotalBalance(BigDecimal.ZERO);
        reportDto.setTotalFee(BigDecimal.ZERO);
        reportDto.setTotalOrderCount(0L);
        reportDto.setTotalPayfor(BigDecimal.ZERO);
        reportDto.setTotalPayforCount(0L);
        reportDto.setTotalOrderCount(0L);
        reportDto.setTotalAmount(BigDecimal.ZERO);
        reportDto.setTotalFreeze(BigDecimal.ZERO);
        reportDto.setTotalPayforFee(BigDecimal.ZERO);
        reportDto.setTotalPayforRate(BigDecimal.ZERO);
        reportDto.setTotalOrderAmount(BigDecimal.ZERO);
        reportDto.setTotalWithdraw(BigDecimal.ZERO);
        return reportDto;
    }

    public static DataReportDto buildNUllEmpty() {
        DataReportDto reportDto = new DataReportDto();
        reportDto.setCreateTime(LocalDate.now());
        
        return reportDto;
    }
}
