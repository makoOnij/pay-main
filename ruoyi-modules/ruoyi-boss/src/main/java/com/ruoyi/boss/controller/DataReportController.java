package com.ruoyi.boss.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.boss.api.dto.DataReportDto;
import com.ruoyi.boss.domain.dto.EChartsDto;
import com.ruoyi.boss.domain.dto.SeriesDto;
import com.ruoyi.boss.service.IDataReportService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.common.tenant.tenant.TenantContextHolder;
import com.ruoyi.system.api.RemoteTenantService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 财务报控制器
 * 前端访问路由地址为:/pay/report
 *
 * @author ruoyi
 * @date 2023-11-26
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/report")
public class DataReportController extends BaseController {

    private final IDataReportService iDataReportService;
    @DubboReference
    private RemoteTenantService remoteTenantService;
    @GetMapping("/today")
    public R<DataReportDto> getNow() {
        /**
         * 可以查询直属商户订单
         */
        List<Long> ids = new ArrayList<>();
        if (!TenantContextHolder.isAdmin()) {
            List<Long> childTenant = remoteTenantService.getChildTenantAndSelf(LoginHelper.getTenantId());
            if (ObjectUtil.isNotEmpty(childTenant)) {
                ids.addAll(childTenant);
            }
        }
        DataReportDto data = iDataReportService.getData(LocalDate.now(), ids);
        if (data == null)
            data = DataReportDto.buildEmpty();
        return R.ok(data);
    }

    @GetMapping("/7day")
    public R get7DayReport() {

        /**
         * 可以查询直属商户订单
         */
        List<Long> ids = new ArrayList<>();
        if (!TenantContextHolder.isAdmin()) {
            List<Long> childTenant = remoteTenantService.getChildTenantAndSelf(LoginHelper.getTenantId());
            if (ObjectUtil.isNotEmpty(childTenant)) {
                ids.addAll(childTenant);
            }
        }
        List<DataReportDto> data = iDataReportService.getDatas(LocalDate.now().minusDays(7), ids);
        if (ObjectUtil.isEmpty(data)) {
            return R.ok(EChartsDto.buildEmpty());
        }

        EChartsDto chartsDto = new EChartsDto();

        List<SeriesDto> seriesDtos = new ArrayList<>();
        //构建每日余额
        SeriesDto mdBalance = new SeriesDto();
        mdBalance.setName("余额金额");
        mdBalance.setData(data.stream().map(p -> p.getTotalBalance().doubleValue()).toArray());
        seriesDtos.add(mdBalance);
        SeriesDto mdCharge = new SeriesDto();
        mdCharge.setName("充值金额");
        mdCharge.setData(data.stream().map(p -> p.getTotalCharge().doubleValue()).toArray());
        seriesDtos.add(mdCharge);
        SeriesDto mdWithdraw = new SeriesDto();
        mdWithdraw.setName("提现金额");
        mdWithdraw.setData(data.stream().map(p -> p.getTotalWithdraw().doubleValue()).toArray());
        seriesDtos.add(mdWithdraw);
        SeriesDto mdPayfor = new SeriesDto();
        mdPayfor.setName("代付金额");
        mdPayfor.setData(data.stream().map(p -> p.getTotalPayfor().doubleValue()).toArray());
        seriesDtos.add(mdPayfor);
        SeriesDto mdPayforFee = new SeriesDto();
        mdPayforFee.setName("代付手续费");
        mdPayforFee.setData(data.stream().map(p -> p.getTotalPayfor().doubleValue()).toArray());
        seriesDtos.add(mdPayforFee);

        chartsDto.setSeriesDtos(seriesDtos);
        chartsDto.setLegend(new String[]{"余额金额", "充值金额", "提现金额", "代付金额", "代付手续费"});
        chartsDto.setDays(data.stream().map(DataReportDto::getCreateTime).toArray());


        return R.ok(chartsDto);
    }
    /**
     * 查询财务报列表
     */
    @SaCheckPermission("pay:report:list")
    @GetMapping("/list")
    public TableDataInfo<DataReportDto> list(DataReportDto bo, PageQuery pageQuery) {
        List<Long> ids = new ArrayList<>();
        List<Long> childTenant = remoteTenantService.getChildTenantAndSelf(LoginHelper.getTenantId());
        if (ObjectUtil.isNotEmpty(childTenant)) {
            ids.addAll(childTenant);
        }
        bo.setIds(ids);
        return iDataReportService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出财务报列表
     */
    @SaCheckPermission("pay:report:export")
    @Log(title = "财务报", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DataReportDto bo, HttpServletResponse response) {
        List<Long> ids = new ArrayList<>();
        List<Long> childTenant = remoteTenantService.getChildTenantAndSelf(LoginHelper.getTenantId());
        if (ObjectUtil.isNotEmpty(childTenant)) {
            ids.addAll(childTenant);
        }
        bo.setIds(ids);
        List<DataReportDto> list = iDataReportService.queryList(bo);
        ExcelUtil.exportExcel(list, "财务报", DataReportDto.class, response);
    }

    /**
     * 获取财务报详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("pay:report:query")
    @GetMapping("/{id}")
    public R<DataReportDto> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iDataReportService.queryById(id));
    }

    /**
     * 删除财务报
     *
     * @param ids 主键串
     */
    @SaCheckPermission("pay:report:remove")
    @Log(title = "财务报", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iDataReportService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
