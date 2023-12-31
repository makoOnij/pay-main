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
import com.ruoyi.order.api.dto.OrderPayforDto;
import com.ruoyi.order.service.IOrderPayforService;
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
 * 代付订单控制器
 * 前端访问路由地址为:/order/payfor
 *
 * @author ruoyi
 * @date 2023-11-22
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/payfor")
public class OrderPayforController extends BaseController {

    private final IOrderPayforService iOrderPayforService;
    @DubboReference
    private RemoteTenantService remoteTenantService;
    /**
     * 查询代付订单列表
     */
    @SaCheckPermission("order:payfor:list")
    @GetMapping("/list")
    public TableDataInfo<OrderPayforDto> list(OrderPayforDto bo, PageQuery pageQuery) {
        if (!TenantContextHolder.isAdmin()) {
            List<Long> childTenant = remoteTenantService.getChildTenantAndSelf(LoginHelper.getTenantId());
            if (ObjectUtil.isNotEmpty(childTenant)) {
                bo.setTenantIds(childTenant);
            }
        }
        return iOrderPayforService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出代付订单列表
     */
    @SaCheckPermission("order:payfor:export")
    @Log(title = "代付订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(OrderPayforDto bo, HttpServletResponse response) {
        List<OrderPayforDto> list = iOrderPayforService.queryList(bo);
        ExcelUtil.exportExcel(list, "代付订单", OrderPayforDto.class, response);
    }

    /**
     * 获取代付订单详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("order:payfor:query")
    @GetMapping("/{id}")
    public R<OrderPayforDto> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iOrderPayforService.queryById(id));
    }

    /**
     * 新增代付订单
     */
    @SaCheckPermission("order:payfor:add")
    @Log(title = "代付订单", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody OrderPayforDto bo) {
        return toAjax(iOrderPayforService.insertByBo(bo));
    }

    /**
     * 修改代付订单
     */
    @SaCheckPermission("order:payfor:edit")
    @Log(title = "代付订单", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody OrderPayforDto bo) {
        return toAjax(iOrderPayforService.updateByBo(bo));
    }

    @SaCheckPermission("order:payfor:audit")
    @Log(title = "代付订单审核", businessType = BusinessType.AUDIT)
    @PutMapping("/audit")
    public R<Void> audit(@RequestBody OrderPayforDto bo) {
        return toAjax(iOrderPayforService.audit(bo));
    }

    /**
     * 删除代付订单
     *
     * @param ids 主键串
     */
    @SaCheckPermission("order:payfor:remove")
    @Log(title = "代付订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iOrderPayforService.deleteWithValidByIds(Arrays.asList(ids), true));
    }

    @SaCheckPermission("order:payfor:channel")
    @Log(title = "代付订单审核", businessType = BusinessType.AUDIT)
    @PutMapping("/channel/{id}")
    public R<Void> channel(@PathVariable Long id) {
        return toAjax(iOrderPayforService.channel(id));
    }
}
