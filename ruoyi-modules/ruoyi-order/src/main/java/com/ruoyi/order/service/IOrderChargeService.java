package com.ruoyi.order.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.order.api.dto.OrderChargeDto;
import com.ruoyi.order.domain.OrderCharge;

import java.util.Collection;
import java.util.List;

/**
 * 商户充值订单Service接口
 *
 * @author ruoyi
 * @date 2023-11-22
 */
public interface IOrderChargeService {

    /**
     * 查询商户充值订单
     */
    OrderChargeDto queryById(Long id);

    /**
     * 查询商户充值订单列表
     */
    TableDataInfo<OrderCharge> queryPageList(OrderChargeDto bo, PageQuery pageQuery);

    /**
     * 查询商户充值订单列表
     */
    List<OrderChargeDto> queryList(OrderChargeDto bo);

    /**
     * 修改商户充值订单
     */
    Boolean insertByBo(OrderChargeDto bo);

    /**
     * 修改商户充值订单
     */
    Boolean updateByBo(OrderChargeDto bo);

    /**
     * 校验并批量删除商户充值订单信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 审核订单
     * @param bo
     * @return
     */
    void audit(OrderChargeDto bo);
}
