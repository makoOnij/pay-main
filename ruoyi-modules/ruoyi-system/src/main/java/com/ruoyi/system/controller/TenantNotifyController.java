package com.ruoyi.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.dto.TenantNotifyDto;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.system.service.ITenantNotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 商户通知控制器
 * 前端访问路由地址为:/system/notify
 *
 * @author ruoyi
 * @date 2023-11-27
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/notify")
public class TenantNotifyController extends BaseController {

    private final ITenantNotifyService iTenantNotifyService;

    /**
     * 查询商户通知列表
     */
    @SaCheckPermission("system:notify:list")
    @GetMapping("/list")
    public TableDataInfo<TenantNotifyDto> list(TenantNotifyDto bo, PageQuery pageQuery) {
        return iTenantNotifyService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出商户通知列表
     */
    @SaCheckPermission("system:notify:export")
    @Log(title = "商户通知", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(TenantNotifyDto bo, HttpServletResponse response) {
        List<TenantNotifyDto> list = iTenantNotifyService.queryList(bo);
        ExcelUtil.exportExcel(list, "商户通知", TenantNotifyDto.class, response);
    }

    /**
     * 获取商户通知详细信息
     *
     * @param notifyId 主键
     */
    @SaCheckPermission("system:notify:query")
    @GetMapping("/{notifyId}")
    public R<TenantNotifyDto> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long notifyId) {
        return R.ok(iTenantNotifyService.queryById(notifyId));
    }

    /**
     * 新增商户通知
     */
    @SaCheckPermission("system:notify:add")
    @Log(title = "商户通知", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody TenantNotifyDto bo) {
        return toAjax(iTenantNotifyService.insertByBo(bo));
    }

    /**
     * 修改商户通知
     */
    @SaCheckPermission("system:notify:edit")
    @Log(title = "商户通知", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody TenantNotifyDto bo) {
        return toAjax(iTenantNotifyService.updateByBo(bo));
    }

    /**
     * 删除商户通知
     *
     * @param notifyIds 主键串
     */
    @SaCheckPermission("system:notify:remove")
    @Log(title = "商户通知", businessType = BusinessType.DELETE)
    @DeleteMapping("/{notifyIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] notifyIds) {
        return toAjax(iTenantNotifyService.deleteWithValidByIds(Arrays.asList(notifyIds), true));
    }
}
