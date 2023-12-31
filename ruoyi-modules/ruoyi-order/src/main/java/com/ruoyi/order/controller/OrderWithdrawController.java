package com.ruoyi.order.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.common.tenant.tenant.TenantContextHolder;
import com.ruoyi.order.api.dto.AuditWithdrawDto;
import com.ruoyi.order.api.dto.OrderWithdrawBo;
import com.ruoyi.order.domain.vo.OrderWithdrawVo;
import com.ruoyi.order.service.IOrderWithdrawService;
import com.ruoyi.system.api.RemoteTenantService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 提现订单控制器
 * 前端访问路由地址为:/order/withdraw
 *
 * @author ruoyi
 * @date 2023-11-22
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/withdraw")
public class OrderWithdrawController extends BaseController {

    private final IOrderWithdrawService iOrderWithdrawService;
    @DubboReference
    private RemoteTenantService remoteTenantService;
    /**
     * 查询提现订单列表
     */
    @SaCheckPermission("order:withdraw:list")
    @GetMapping("/list")
    public TableDataInfo<OrderWithdrawVo> list(OrderWithdrawBo bo, PageQuery pageQuery) {
        if (!TenantContextHolder.isAdmin()) {
            List<Long> childTenant = remoteTenantService.getChildTenantAndSelf(LoginHelper.getTenantId());
            if (ObjectUtil.isNotEmpty(childTenant)) {
                bo.setTenantIds(childTenant);
            }
        }
        return iOrderWithdrawService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出提现订单列表
     */
    @SaCheckPermission("order:withdraw:export")
    @Log(title = "提现订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(OrderWithdrawBo bo, HttpServletResponse response) {
        if (!TenantContextHolder.isAdmin()) {
            List<Long> childTenant = remoteTenantService.getChildTenantAndSelf(LoginHelper.getTenantId());
            if (ObjectUtil.isNotEmpty(childTenant)) {
                bo.setTenantIds(childTenant);
            }
        }
        List<OrderWithdrawVo> list = iOrderWithdrawService.queryList(bo);
        ExcelUtil.exportExcel(list, "提现订单", OrderWithdrawVo.class, response);
    }

    /**
     * 获取提现订单详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("order:withdraw:query")
    @GetMapping("/{id}")
    public R<OrderWithdrawVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iOrderWithdrawService.queryById(id));
    }

    /**
     * 新增提现订单
     */
    @SaCheckPermission("order:withdraw:add")
    @Log(title = "提现订单", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody OrderWithdrawBo bo) {
        return toAjax(iOrderWithdrawService.insertByBo(bo));
    }

    /**
     * 修改提现订单
     */
    @SaCheckPermission("order:withdraw:edit")
    @Log(title = "提现订单", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody OrderWithdrawBo bo) {
        return toAjax(iOrderWithdrawService.updateByBo(bo));
    }

    /**
     * 删除提现订单
     *
     * @param ids 主键串
     */
    @SaCheckPermission("order:withdraw:remove")
    @Log(title = "提现订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iOrderWithdrawService.deleteWithValidByIds(Arrays.asList(ids), true));
    }

    /**
     * 审核提现订单
     */
    @SaCheckPermission("order:withdraw:audit")
    @Log(title = "提现订单", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AuditWithdrawDto bo) {
        return toAjax(iOrderWithdrawService.audit(bo));
    }
}
