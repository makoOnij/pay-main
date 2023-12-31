package com.ruoyi.order.controller;

import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.SeqKit;
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
import com.ruoyi.order.api.dto.OrderChargeDto;
import com.ruoyi.order.domain.OrderCharge;
import com.ruoyi.order.service.IOrderChargeService;
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
 * 商户充值订单控制器
 * 前端访问路由地址为:/order/merchant
 *
 * @author ruoyi
 * @date 2023-11-22
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/charge")
public class OrderChargeController extends BaseController {

    private final IOrderChargeService iOrderChargeService;
    @DubboReference
    private RemoteTenantService remoteTenantService;
    /**
     * 查询商户充值订单列表
     */
    @SaCheckPermission("order:charge:list")
    @GetMapping("/list")
    public TableDataInfo<OrderCharge> list(OrderChargeDto bo, PageQuery pageQuery) {
        /**
         * 可以查询直属商户订单
         */

        if (!TenantContextHolder.isAdmin()) {
            List<Long> childTenant = remoteTenantService.getChildTenantAndSelf(LoginHelper.getTenantId());
            if (ObjectUtil.isNotEmpty(childTenant)) {
                bo.setTenantIds(childTenant);
            }
        }
        return iOrderChargeService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出商户充值订单列表
     */
    @SaCheckPermission("order:charge:export")
    @Log(title = "商户充值订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(OrderChargeDto bo, HttpServletResponse response) {
        if (!TenantContextHolder.isAdmin()) {
            List<Long> childTenant = remoteTenantService.getChildTenantAndSelf(LoginHelper.getTenantId());
            if (ObjectUtil.isNotEmpty(childTenant)) {
                bo.setTenantIds(childTenant);
            }
        }
        List<OrderChargeDto> list = iOrderChargeService.queryList(bo);
        ExcelUtil.exportExcel(list, "商户充值订单", OrderChargeDto.class, response);
    }

    /**
     * 获取商户充值订单详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("order:charge:query")
    @GetMapping("/{id}")
    public R<OrderChargeDto> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iOrderChargeService.queryById(id));
    }

    /**
     * 新增商户充值订单
     */
    @SaCheckPermission("order:charge:add")
    @Log(title = "商户充值订单", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody OrderChargeDto bo) {
        bo.setOrderNo(SeqKit.genMerchantChargeId());
        return toAjax(iOrderChargeService.insertByBo(bo));
    }

    /**
     * 修改商户充值订单
     */
    @SaCheckPermission("order:charge:edit")
    @Log(title = "商户充值订单", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody OrderChargeDto bo) {
        return toAjax(iOrderChargeService.updateByBo(bo));
    }

    /**
     * 删除商户充值订单
     *
     * @param ids 主键串
     */
    @SaCheckPermission("order:charge:remove")
    @Log(title = "商户充值订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iOrderChargeService.deleteWithValidByIds(Arrays.asList(ids), true));
    }


    @SaCheckOr(permission = @SaCheckPermission("order:charge:audit"), role = {@SaCheckRole("channel_account"), @SaCheckRole("admin")})
    @Log(title = "商户充值订单", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public R<Void> audit(@Validated(EditGroup.class) @RequestBody OrderChargeDto bo) {
        iOrderChargeService.audit(bo);
        return R.ok("操作成功");
    }
}
