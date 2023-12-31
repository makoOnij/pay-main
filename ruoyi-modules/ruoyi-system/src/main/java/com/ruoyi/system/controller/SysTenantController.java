package com.ruoyi.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.enums.TenantType;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.tenant.annotation.IgnoreTenant;
import com.ruoyi.system.domain.bo.SysTenantBo;
import com.ruoyi.system.domain.vo.SysTenantVo;
import com.ruoyi.system.service.ISysTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 租户控制器
 * 前端访问路由地址为:/system/tenant
 *
 * @author ruoyi
 * @date 2023-11-20
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/tenant")
public class SysTenantController extends BaseController {

    private final ISysTenantService iSysTenantService;

    /**
     * 查询租户列表
     */
    @SaCheckPermission("system:tenant:list")
    @GetMapping("/list")
    public TableDataInfo<SysTenantVo> list(SysTenantBo bo, PageQuery pageQuery) {
        bo.setTenantType(TenantType.CHANNEL.getCode());
        return iSysTenantService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出租户列表
     */
    @SaCheckPermission("system:tenant:export")
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
    @SaCheckPermission("system:tenant:query")
    @GetMapping("/{id}")
    @IgnoreTenant
    public R<SysTenantVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iSysTenantService.queryById(id));
    }

    /**
     * 新增租户
     */
    @SaCheckPermission("system:tenant:add")
    @Log(title = "租户", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysTenantBo bo) {
        return toAjax(iSysTenantService.insertByBo(bo));
    }

    /**
     * 修改租户
     */
    @SaCheckPermission("system:tenant:edit")
    @Log(title = "租户", businessType = BusinessType.UPDATE)
    @PutMapping()
    @IgnoreTenant
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysTenantBo bo) {
        return toAjax(iSysTenantService.updateByBo(bo));
    }

    @Log(title = "租户", businessType = BusinessType.UPDATE)
    @PutMapping("/api")
    @IgnoreTenant
    public R<Void> editApi(@Validated(EditGroup.class) @RequestBody SysTenantBo bo) {
        return toAjax(iSysTenantService.updateApi(bo));
    }

    /**
     * 删除租户
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:tenant:remove")
    @Log(title = "租户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @IgnoreTenant
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iSysTenantService.deleteWithValidByIds(Arrays.asList(ids), true));
    }

}
