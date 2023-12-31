package com.ruoyi.boss.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.boss.api.domain.TenantWayDto;
import com.ruoyi.boss.domain.bo.TenantChannelBo;
import com.ruoyi.boss.domain.dto.EditWayStatusDto;
import com.ruoyi.boss.domain.vo.TenantChannelVo;
import com.ruoyi.boss.service.ITenantChannelService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 代付通道控制器
 * 前端访问路由地址为:/pay/mchway
 *
 * @author ruoyi
 * @date 2023-11-24
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/mchway")
public class TenantChannelController extends BaseController {

    private final ITenantChannelService iTenantChannelService;

    /**
     * 查询代付通道列表
     */
    @SaCheckPermission("pay:mchway:list")
    @GetMapping("/list")
    public TableDataInfo<TenantChannelVo> list(TenantChannelBo bo, PageQuery pageQuery) {
        return iTenantChannelService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出代付通道列表
     */
    @SaCheckPermission("pay:mchway:export")
    @Log(title = "代付通道", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(TenantChannelBo bo, HttpServletResponse response) {
        List<TenantChannelVo> list = iTenantChannelService.queryList(bo);
        ExcelUtil.exportExcel(list, "代付通道", TenantChannelVo.class, response);
    }

    /**
     * 获取代付通道详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("pay:mchway:query")
    @GetMapping("/{id}")
    public R<TenantChannelVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iTenantChannelService.queryById(id));
    }

    @SaCheckPermission("pay:mchway:query")
    @GetMapping("/all")
    public R getAll() {
        return R.ok(iTenantChannelService.queryList(new TenantChannelBo()));
    }

    @SaCheckPermission("pay:mchway:query")
    @GetMapping("/myWay")
    public R getMyWay() {
        TenantChannelBo tenantChannelBo = new TenantChannelBo();
        tenantChannelBo.setTenantId(LoginHelper.getLoginUser().getTenantId());
        return R.ok(iTenantChannelService.queryList(tenantChannelBo));
    }

    /**
     * 新增代付通道
     */
    @SaCheckPermission("pay:mchway:add")
    @Log(title = "代付通道", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody TenantChannelBo bo) {
        return toAjax(iTenantChannelService.insertByBo(bo));
    }

    /**
     * 修改代付通道
     */
    @SaCheckPermission("pay:mchway:edit")
    @Log(title = "代付通道", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody TenantChannelBo bo) {
        return toAjax(iTenantChannelService.updateByBo(bo));
    }

    /**
     * 删除代付通道
     *
     * @param ids 主键串
     */
    @SaCheckPermission("pay:mchway:remove")
    @Log(title = "代付通道", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iTenantChannelService.deleteWithValidByIds(Arrays.asList(ids), true));
    }

    /**
     * 修改通道状态
     *
     * @param bo
     * @return
     */
    @SaCheckPermission("pay:mchway:edit")
    @Log(title = "通道列表", businessType = BusinessType.UPDATE)
    @PutMapping("/channel")
    public R<Void> editChannel(@Validated(EditGroup.class) @RequestBody EditWayStatusDto bo) {
        return toAjax(iTenantChannelService.updateChannel(bo.getIds(), bo.getMerchantWay()));
    }

    @SaCheckPermission("pay:mchway:edit")
    @Log(title = "通道列表", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public R<Void> editStatus(@Validated(EditGroup.class) @RequestBody TenantWayDto bo) {
        return toAjax(iTenantChannelService.updateStatus(bo.getId(), bo.getStatus()));
    }
}
