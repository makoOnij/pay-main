package com.ruoyi.order.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.order.api.dto.OrderChannelBo;
import com.ruoyi.order.domain.vo.OrderChannelVo;
import com.ruoyi.order.service.IOrderChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 渠道充值订单控制器
 * 前端访问路由地址为:/order/channel
 *
 * @author ruoyi
 * @date 2023-11-22
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/channel")
public class OrderChannelController extends BaseController {

    private final IOrderChannelService iOrderChannelService;

    /**
     * 查询渠道充值订单列表
     */
    @SaCheckPermission("order:channel:list")
    @GetMapping("/list")
    public TableDataInfo<OrderChannelVo> list(OrderChannelBo bo, PageQuery pageQuery) {
        return iOrderChannelService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出渠道充值订单列表
     */
    @SaCheckPermission("order:channel:export")
    @Log(title = "渠道充值订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(OrderChannelBo bo, HttpServletResponse response) {
        List<OrderChannelVo> list = iOrderChannelService.queryList(bo);
        ExcelUtil.exportExcel(list, "渠道充值订单", OrderChannelVo.class, response);
    }

    /**
     * 获取渠道充值订单详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("order:channel:query")
    @GetMapping("/{id}")
    public R<OrderChannelVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iOrderChannelService.queryById(id));
    }

    /**
     * 新增渠道充值订单
     */
    @SaCheckPermission("order:channel:add")
    @Log(title = "渠道充值订单", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody OrderChannelBo bo) {
        return toAjax(iOrderChannelService.insertByBo(bo));
    }

    /**
     * 修改渠道充值订单
     */
    @SaCheckPermission("order:channel:edit")
    @Log(title = "渠道充值订单", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody OrderChannelBo bo) {
        return toAjax(iOrderChannelService.updateByBo(bo));
    }

    /**
     * 删除渠道充值订单
     *
     * @param ids 主键串
     */
    @SaCheckPermission("order:channel:remove")
    @Log(title = "渠道充值订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iOrderChannelService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
