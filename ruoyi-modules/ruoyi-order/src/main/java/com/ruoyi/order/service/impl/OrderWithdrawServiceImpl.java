package com.ruoyi.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.boss.api.RemoteBossService;
import com.ruoyi.boss.api.dto.DataFlowDto;
import com.ruoyi.boss.api.dto.DataReportDto;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.common.core.enums.AuditStatus;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.SeqKit;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.order.api.dto.AuditWithdrawDto;
import com.ruoyi.order.api.dto.OrderWithdrawBo;
import com.ruoyi.order.domain.OrderWithdraw;
import com.ruoyi.order.domain.vo.OrderWithdrawVo;
import com.ruoyi.order.mapper.OrderWithdrawMapper;
import com.ruoyi.order.service.IOrderWithdrawService;
import com.ruoyi.resource.api.RemoteMessageService;
import com.ruoyi.resource.api.domain.WebsocketMessage;
import com.ruoyi.system.api.RemoteTenantService;
import com.ruoyi.system.api.model.LoginUser;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 提现订单Service业务层处理
 *
 * @author ruoyi
 * @date 2023-11-22
 */
@RequiredArgsConstructor
@Service
public class OrderWithdrawServiceImpl implements IOrderWithdrawService {

    private final OrderWithdrawMapper baseMapper;

    @DubboReference
    private RemoteBossService remoteBossService;

    @DubboReference
    private RemoteTenantService remoteTenantService;

    @DubboReference
    private RemoteMessageService remoteMessageService;

    /**
     * 查询提现订单
     */
    @Override
    public OrderWithdrawVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询提现订单列表
     */
    @Override
    public TableDataInfo<OrderWithdrawVo> queryPageList(OrderWithdrawBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OrderWithdraw> lqw = buildQueryWrapper(bo);
        Page<OrderWithdrawVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询提现订单列表
     */
    @Override
    public List<OrderWithdrawVo> queryList(OrderWithdrawBo bo) {
        LambdaQueryWrapper<OrderWithdraw> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<OrderWithdraw> buildQueryWrapper(OrderWithdrawBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<OrderWithdraw> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getTenantId() != null, OrderWithdraw::getTenantId, bo.getTenantId());
        lqw.eq(StringUtils.isNotBlank(bo.getTenantName()), OrderWithdraw::getTenantName, bo.getTenantName());
        lqw.eq(StringUtils.isNotBlank(bo.getOrderNo()), OrderWithdraw::getOrderNo, bo.getOrderNo());
        lqw.like(StringUtils.isNotBlank(bo.getUserName()), OrderWithdraw::getUserName, bo.getUserName());
        lqw.eq(StringUtils.isNotBlank(bo.getWithdrawType()), OrderWithdraw::getWithdrawType, bo.getWithdrawType());
        lqw.eq(StringUtils.isNotBlank(bo.getUserAccount()), OrderWithdraw::getUserAccount, bo.getUserAccount());
        lqw.eq(StringUtils.isNotBlank(bo.getUserAccountNo()), OrderWithdraw::getUserAccountNo, bo.getUserAccountNo());
        lqw.eq(bo.getAmount() != null, OrderWithdraw::getAmount, bo.getAmount());
        lqw.eq(bo.getRate() != null, OrderWithdraw::getRate, bo.getRate());
        lqw.eq(bo.getRealAmount() != null, OrderWithdraw::getRealAmount, bo.getRealAmount());
        lqw.eq(bo.getStatus() != null, OrderWithdraw::getStatus, bo.getStatus());
        lqw.in(bo.getTenantIds() != null, OrderWithdraw::getTenantId, bo.getTenantIds());
        lqw.orderByDesc(OrderWithdraw::getCreateTime);
        return lqw;
    }

    /**
     * 新增提现订单
     */
    @Override
    public Boolean insertByBo(OrderWithdrawBo bo) {
        OrderWithdraw add = BeanUtil.toBean(bo, OrderWithdraw.class);
        add.setOrderNo(SeqKit.genWithdrawOrderId());
        LoginUser loginUser = LoginHelper.getLoginUser();
        add.setTenantId(loginUser.getTenantId());
        add.setTenantName(loginUser.getTenantName());
        TenantDto channel = remoteTenantService.getChannel(loginUser.getTenantId());
        if (ObjectUtil.isEmpty(channel)) {
            throw new ServiceException("未找到渠道");
        }
        add.setParentName(channel.getName());
        add.setParentId(channel.getId());
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改提现订单
     */
    @Override
    public Boolean updateByBo(OrderWithdrawBo bo) {
        OrderWithdraw update = BeanUtil.toBean(bo, OrderWithdraw.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(OrderWithdraw entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除提现订单
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 修改订单状态
     *
     * @param data@return
     */
    @Override
    public int audit(AuditWithdrawDto data) {
        OrderWithdraw orderWithdraw = baseMapper.selectById(data.getId());
        if (orderWithdraw == null) {
            throw new ServiceException("订单信息错误");
        }
        if (!orderWithdraw.getStatus().equals(AuditStatus.WAIT.getCode())) {
            throw new ServiceException("订单已审核");
        }
        TenantDto tenant = remoteTenantService.getTenant(orderWithdraw.getTenantId());
        if (ObjectUtil.isEmpty(tenant)) {
            throw new ServiceException("商户信息错误");
        }

        orderWithdraw.setStatus(data.getStatus());

        baseMapper.update(Wrappers.<OrderWithdraw>update().eq("id", data.getId())
                .set("status", data.getStatus())
                .set(data.getRemark() != null, "remark", data.getRemark())
        );
        process(data, tenant, orderWithdraw);
        return 1;
    }

    @Async
    public void process(AuditWithdrawDto data, TenantDto tenant, OrderWithdraw orderWithdraw) {
        remoteBossService.insertFlow(DataFlowDto.buildWithdraw(tenant, orderWithdraw.getRealAmount()));
        remoteBossService.inertReport(DataReportDto.buildWithdraw(tenant, orderWithdraw.getRealAmount()));
        //websocket 推送
        WebsocketMessage message = WebsocketMessage.build(WebsocketMessage.MessageType.WITHDRAW, data.getStatus().equals(AuditStatus.SUCCESS.getCode()) ? "您的提现审核已经通过!" : "您的提现申请被拒绝!");

        remoteMessageService.sendMessageTenant(tenant.getId(), message);
    }

}
