package com.ruoyi.boss.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.boss.api.dto.DataFlowDto;
import com.ruoyi.boss.service.IDataFlowService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.system.api.RemoteTenantService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 资金明细控制器
 * 前端访问路由地址为:/pay/flow
 *
 * @author ruoyi
 * @date 2023-11-26
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/flow")
public class DataFlowController extends BaseController {

    private final IDataFlowService iDataFlowService;

    @DubboReference
    private RemoteTenantService remoteTenantService;

    /**
     * 查询资金明细列表
     */
    @SaCheckPermission("pay:flow:list")
    @GetMapping("/list")
    public TableDataInfo<DataFlowDto> list(DataFlowDto bo, PageQuery pageQuery) {
        List<Long> ids = new ArrayList<>();
        List<Long> childTenant = remoteTenantService.getChildTenantAndSelf(LoginHelper.getTenantId());
        if (ObjectUtil.isNotEmpty(childTenant)) {
            ids.addAll(childTenant);
        }
        bo.setTenantIds(ids);
        return iDataFlowService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出资金明细列表
     */
    @SaCheckPermission("pay:flow:export")
    @Log(title = "资金明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DataFlowDto bo, HttpServletResponse response) {
        List<Long> ids = new ArrayList<>();
        List<Long> childTenant = remoteTenantService.getChildTenantAndSelf(LoginHelper.getTenantId());
        if (ObjectUtil.isNotEmpty(childTenant)) {
            ids.addAll(childTenant);
        }
        bo.setTenantIds(ids);
        List<DataFlowDto> list = iDataFlowService.queryList(bo);
        ExcelUtil.exportExcel(list, "资金明细", DataFlowDto.class, response);
    }

    /**
     * 获取资金明细详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("pay:flow:query")
    @GetMapping("/{id}")
    public R<DataFlowDto> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iDataFlowService.queryById(id));
    }
    /**
     * 删除资金明细
     *
     * @param ids 主键串
     */
    @SaCheckPermission("pay:flow:remove")
    @Log(title = "资金明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iDataFlowService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
