package com.ruoyi.boss.controller;

import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.ruoyi.boss.domain.PayChannel;
import com.ruoyi.boss.domain.bo.PayChannelBo;
import com.ruoyi.boss.domain.dto.PayChannelBatchDto;
import com.ruoyi.boss.domain.vo.PayChannelVo;
import com.ruoyi.boss.service.IPayChannelService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 通道列表控制器
 * 前端访问路由地址为:/pay/way
 *
 * @author ruoyi
 * @date 2023-11-24
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/way")
public class PayChannelController extends BaseController {

    private final IPayChannelService iPayChannelService;

    /**
     * 查询通道列表列表
     */
    @SaCheckOr(permission =@SaCheckPermission("pay:way:list"), role =@SaCheckRole("channel_account"))
    @GetMapping("/list")
    public TableDataInfo<PayChannelVo> list(PayChannelBo bo, PageQuery pageQuery) {
        return iPayChannelService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出通道列表列表
     */
    @SaCheckPermission("pay:way:export")
    @Log(title = "通道列表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PayChannelBo bo, HttpServletResponse response) {
        List<PayChannelVo> list = iPayChannelService.queryList(bo);
        ExcelUtil.exportExcel(list, "通道列表", PayChannelVo.class, response);
    }

    /**
     * 获取通道列表详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("pay:way:query")
    @GetMapping("/{id}")
    public R<PayChannelVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iPayChannelService.queryById(id));
    }


    @GetMapping("/myList")
    public R myList() {
        return R.ok(iPayChannelService.queryValidList());
    }

    /**
     * 新增通道列表
     */
    @SaCheckPermission("pay:way:add")
    @Log(title = "通道列表", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PayChannelBo bo) {
        return toAjax(iPayChannelService.insertByBo(bo));
    }

    /**
     * 修改通道列表
     */
    @SaCheckPermission("pay:way:edit")
    @Log(title = "通道列表", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PayChannelBo bo) {
        return toAjax(iPayChannelService.updateByBo(bo));
    }


    /**
     * 删除通道列表
     *
     * @param ids 主键串
     */
    @SaCheckPermission("pay:way:remove")
    @Log(title = "通道列表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iPayChannelService.deleteWithValidByIds(Arrays.asList(ids), true));
    }

    @SaCheckPermission("pay:way:edit")
    @Log(title = "通道列表", businessType = BusinessType.UPDATE)
    @PutMapping("/change/{ids}")
    public R<Void> change(@RequestBody PayChannelBatchDto payWayBo) {
        return toAjax(iPayChannelService.updateStatusByIds(payWayBo.getIds(), payWayBo.getStatus()));
    }

    @SaCheckPermission("pay:way:edit")
    @Log(title = "通道列表", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public R<Void> editStatus(@RequestBody PayChannel bo) {
        return toAjax(iPayChannelService.updateStatus(bo.getId(), bo.getStatus()));
    }
}
