package com.ruoyi.system.service;

import com.ruoyi.common.core.dto.TenantNotifyDto;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 商户通知Service接口
 *
 * @author ruoyi
 * @date 2023-11-27
 */
public interface ITenantNotifyService {

    /**
     * 查询商户通知
     */
    TenantNotifyDto queryById(Long notifyId);

    /**
     * 查询商户通知列表
     */
    TableDataInfo<TenantNotifyDto> queryPageList(TenantNotifyDto bo, PageQuery pageQuery);

    /**
     * 查询商户通知列表
     */
    List<TenantNotifyDto> queryList(TenantNotifyDto bo);

    /**
     * 修改商户通知
     */
    Boolean insertByBo(TenantNotifyDto bo);

    /**
     * 修改商户通知
     */
    Boolean updateByBo(TenantNotifyDto bo);

    /**
     * 校验并批量删除商户通知信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
