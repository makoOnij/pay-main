package com.ruoyi.order.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.order.api.dto.OrderChannelBo;
import com.ruoyi.order.domain.vo.OrderChannelVo;

import java.util.Collection;
import java.util.List;

/**
 * 渠道充值订单Service接口
 *
 * @author ruoyi
 * @date 2023-11-22
 */
public interface IOrderChannelService {

    /**
     * 查询渠道充值订单
     */
    OrderChannelVo queryById(Long id);

    /**
     * 查询渠道充值订单列表
     */
    TableDataInfo<OrderChannelVo> queryPageList(OrderChannelBo bo, PageQuery pageQuery);

    /**
     * 查询渠道充值订单列表
     */
    List<OrderChannelVo> queryList(OrderChannelBo bo);

    /**
     * 修改渠道充值订单
     */
    Boolean insertByBo(OrderChannelBo bo);

    /**
     * 修改渠道充值订单
     */
    Boolean updateByBo(OrderChannelBo bo);

    /**
     * 校验并批量删除渠道充值订单信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
