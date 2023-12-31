package com.ruoyi.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.common.core.enums.TenantType;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.UsdtKit;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.common.tenant.annotation.IgnoreTenant;
import com.ruoyi.common.tenant.tenant.TenantBroker;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.domain.SysTenant;
import com.ruoyi.system.domain.bo.SysTenantBo;
import com.ruoyi.system.domain.bo.SysTenantChargeDto;
import com.ruoyi.system.domain.dto.BatchTenantDto;
import com.ruoyi.system.domain.dto.UpdateTenantSecurityDto;
import com.ruoyi.system.domain.vo.SysTenantVo;
import com.ruoyi.system.service.ISysTenantService;
import com.ruoyi.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 租户控制器
 * 前端访问路由地址为:/system/merchant
 *
 * @author ruoyi
 * @date 2023-11-24
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/merchant")
public class SysMerchantController extends BaseController {

    private final ISysTenantService iSysTenantService;
    private final ISysUserService userService;

    /**
     * 查询租户列表
     */
    @SaCheckPermission("system:merchant:list")
    @GetMapping("/list")
    public TableDataInfo<SysTenantVo> list(SysTenantBo bo, PageQuery pageQuery) {
        bo.setTenantType(TenantType.MERCHANT.getCode());
        return iSysTenantService.queryPageList(bo, pageQuery);
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:merchant:edit")
    @Log(title = "租户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    @IgnoreTenant
    public R<Void> changeStatus(@RequestBody SysTenant user) {
        return toAjax(iSysTenantService.updateStatus(user));
    }


    @SaCheckPermission("system:merchant:edit")
    @Log(title = "租户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changePay")
    @IgnoreTenant
    public R<Void> changePay(@RequestBody SysTenant user) {
        return toAjax(iSysTenantService.updateStatus(user));
    }

    @SaCheckPermission("system:merchant:edit")
    @Log(title = "租户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPass")
    public R<Void> resetPass(@RequestBody SysTenant user) {
        SysUser sysUser = userService.selectUserByUserName(user.getLoginUser());
        if (ObjectUtil.isEmpty(sysUser)) {
            throw new ServiceException("商户账号不存在");
        }
        String hash = BCrypt.hashpw(user.getLoginPassword());
        return toAjax(userService.resetUserPwd(sysUser.getUserName(), hash));
    }
    /**
     * 导出租户列表
     */
    @SaCheckPermission("system:merchant:export")
    @Log(title = "租户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysTenantBo bo, HttpServletResponse response) {
        List<SysTenantVo> list = iSysTenantService.queryList(bo);
        ExcelUtil.exportExcel(list, "租户", SysTenantVo.class, response);
    }

    /**
     * 获取租户详细信息
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    @IgnoreTenant
    public R<SysTenantVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        SysTenantVo bo = TenantBroker.applyAs(1L, p -> iSysTenantService.queryById(id));
        return R.ok(bo);
    }

    /**
     * 新增租户
     */
    @SaCheckPermission("system:merchant:add")
    @Log(title = "租户", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysTenantBo bo) {
        return toAjax(iSysTenantService.insertByBo(bo));
    }

    /**
     * 修改租户
     */
    @SaCheckPermission("system:merchant:edit")
    @Log(title = "租户", businessType = BusinessType.UPDATE)
    @PutMapping()
    @IgnoreTenant
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysTenantBo bo) {
        return toAjax(iSysTenantService.updateByBo(bo));
    }

    /**
     * 修改安全信息
     *
     * @param bo
     * @return
     */
    @SaCheckPermission("system:merchant:edit")
    @Log(title = "租户", businessType = BusinessType.UPDATE)
    @PutMapping("/updateSecurity")
    @IgnoreTenant
    public R<Void> updateSecurity(@Validated(EditGroup.class) @RequestBody UpdateTenantSecurityDto bo) {
        return toAjax(iSysTenantService.updateSecurity(bo));
    }

    @SaCheckPermission("system:merchant:edit")
    @Log(title = "租户", businessType = BusinessType.UPDATE)
    @PutMapping("/updateAutoPay")
    @IgnoreTenant
    public R<Void> updateAutoPay(@Validated(EditGroup.class) @RequestBody UpdateTenantSecurityDto bo) {
        return toAjax(iSysTenantService.updateAutoPay(bo));
    }

    /**
     * 删除租户
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:merchant:remove")
    @Log(title = "租户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @IgnoreTenant
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iSysTenantService.deleteWithValidByIds(Arrays.asList(ids), true));
    }


    @SaCheckPermission("system:merchant:edit")
    @Log(title = "租户", businessType = BusinessType.UPDATE)
    @PutMapping("/batchWhiteList")
    @IgnoreTenant
    public R<Void> batchWhiteList(@RequestBody BatchTenantDto dto) {
        return toAjax(iSysTenantService.batchUpdateWhiteList(dto.getIds(), dto.getWhiteList()));
    }

    @SaCheckPermission("system:merchant:edit")
    @Log(title = "租户", businessType = BusinessType.UPDATE)
    @PutMapping("/batchStatus")
    @IgnoreTenant
    public R<Void> chargeStatus(@RequestBody BatchTenantDto dto) {
        return toAjax(iSysTenantService.batchUpdateStatus(dto.getIds(), dto.getStatus()));
    }

    @SaCheckPermission("system:merchant:edit")
    @Log(title = "租户", businessType = BusinessType.UPDATE)
    @PutMapping("/batchChargeRate")
    @IgnoreTenant
    public R<Void> chargeRate(@RequestBody BatchTenantDto dto) {
        return toAjax(iSysTenantService.batchUpdateChargeRate(dto));
    }

    @SaCheckPermission("system:merchant:edit")
    @Log(title = "租户", businessType = BusinessType.UPDATE)
    @PutMapping("/batchApiWhiteList")
    @IgnoreTenant
    public R<Void> batchApiWhiteList(@RequestBody BatchTenantDto dto) {
        return toAjax(iSysTenantService.batchUpdateApiWhiteList(dto.getIds(), dto.getApiWhiteList()));
    }
    
    @SaCheckPermission("system:merchant:charge")
    @Log(title = "租户", businessType = BusinessType.CHARGE)
    @PostMapping("/charge")
    @IgnoreTenant
    public R<Void> charge(@Validated(AddGroup.class) @RequestBody SysTenantChargeDto bo) {
        return toAjax(iSysTenantService.amountIncrease(bo));
    }

    @GetMapping("/onlineRate")
    public R getOnlineRate() {
        return R.ok(UsdtKit.getCurrentRate());
    }
    /**
     * 查询USDT汇率
     *
     * @return
     */
    @GetMapping("/rate")
    public R getChargeRate() {
        BigDecimal chargeRate = iSysTenantService.getChargeRate(LoginHelper.getTenantId());
        return R.ok(chargeRate);
    }

    @GetMapping("/withdrawRate")
    public R getWithdrawRate() {
        BigDecimal withdrawRate = iSysTenantService.getWithdrawRate(LoginHelper.getTenantId());
        return R.ok(withdrawRate);
    }

    @GetMapping("/chargeAddress")
    public R getChargeAddress() {

        String withdrawRate = iSysTenantService.getChargeAddress(LoginHelper.getTenantId());
        return R.ok(withdrawRate);
    }

    @GetMapping("/chargeConfig")
    public R getChargeConfig() {

        String chargeAddress = iSysTenantService.getChargeAddress(LoginHelper.getTenantId());
        BigDecimal chargeRate = iSysTenantService.getChargeRate(LoginHelper.getTenantId());
        Map<String, Object> result = new HashMap<>();
        result.put("rate", chargeRate);
        result.put("chargeAddress", chargeAddress);
        return R.ok(result);
    }

    /**
     * 查询下级商户
     *
     * @return
     */
    @GetMapping("/subTenant")
    @IgnoreTenant
    public R subTenant() {
        List<TenantDto> children = iSysTenantService.getChilds(LoginHelper.getTenantId());
        return R.ok(children);
    }
}
