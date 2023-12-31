package com.ruoyi.order.service;

import com.ruoyi.common.core.dto.PayOrderDto;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.order.api.dto.OrderPayforDto;

import java.util.Collection;
import java.util.List;

/**
 * 代付订单Service接口
 *
 * @author ruoyi
 * @date 2023-11-22
 */
public interface IOrderPayforService {

    /**
     * 查询代付订单
     */
    OrderPayforDto queryById(Long id);

    /**
     * 查询代付订单列表
     */
    TableDataInfo<OrderPayforDto> queryPageList(OrderPayforDto bo, PageQuery pageQuery);

    /**
     * 查询代付订单列表
     */
    List<OrderPayforDto> queryList(OrderPayforDto bo);

    /**
     * 修改代付订单
     */
    Boolean insertByBo(OrderPayforDto bo);

    /**
     * 修改代付订单
     */
    Boolean updateByBo(OrderPayforDto bo);

    /**
     * 校验并批量删除代付订单信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 订单审核
     *
     * @param bo
     * @return
     */
    int audit(OrderPayforDto bo);

    boolean updateIng2Success(String orderNo);

    boolean updateIng2Fail(String orderNo, String channelErrCode, String channelErrMsg);

    Long addOrder(PayOrderDto model);

    /**
     * 调起通道出款
     *
     * @param id
     * @return
     */
    int channel(Long id);

    void changeChannel(Long id);
}
