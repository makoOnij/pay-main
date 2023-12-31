package com.ruoyi.order.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.order.api.dto.AuditWithdrawDto;
import com.ruoyi.order.api.dto.OrderWithdrawBo;
import com.ruoyi.order.domain.vo.OrderWithdrawVo;

import java.util.Collection;
import java.util.List;

/**
 * 提现订单Service接口
 *
 * @author ruoyi
 * @date 2023-11-22
 */
public interface IOrderWithdrawService {

    /**
     * 查询提现订单
     */
    OrderWithdrawVo queryById(Long id);

    /**
     * 查询提现订单列表
     */
    TableDataInfo<OrderWithdrawVo> queryPageList(OrderWithdrawBo bo, PageQuery pageQuery);

    /**
     * 查询提现订单列表
     */
    List<OrderWithdrawVo> queryList(OrderWithdrawBo bo);

    /**
     * 修改提现订单
     */
    Boolean insertByBo(OrderWithdrawBo bo);

    /**
     * 修改提现订单
     */
    Boolean updateByBo(OrderWithdrawBo bo);

    /**
     * 校验并批量删除提现订单信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 修改订单状态
     *
     * @param data
     * @return
     */
    int audit(AuditWithdrawDto data);
}
