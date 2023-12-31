package com.ruoyi.boss.service;

import com.ruoyi.boss.api.domain.TenantWayDto;
import com.ruoyi.boss.domain.TenantChannel;
import com.ruoyi.boss.domain.bo.TenantChannelBo;
import com.ruoyi.boss.domain.vo.TenantChannelVo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 代付通道Service接口
 *
 * @author ruoyi
 * @date 2023-11-24
 */
public interface ITenantChannelService {

    /**
     * 查询代付通道
     */
    TenantChannelVo queryById(Long id);

    /**
     * 查询代付通道列表
     */
    TableDataInfo<TenantChannelVo> queryPageList(TenantChannelBo bo, PageQuery pageQuery);

    /**
     * 查询代付通道列表
     */
    List<TenantChannelVo> queryList(TenantChannelBo bo);

    /**
     * 修改代付通道
     */
    Boolean insertByBo(TenantChannelBo bo);

    /**
     * 修改代付通道
     */
    Boolean updateByBo(TenantChannelBo bo);

    /**
     * 校验并批量删除代付通道信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 分配通道
     *
     * @param ids
     * @param merchantWay
     * @return
     */
    int updateChannel(List<Long> ids, TenantChannel merchantWay);

    /**
     * 修改状态
     *
     * @param id
     * @param status
     * @return
     */
    int updateStatus(Long id, Integer status);

    TenantWayDto queryBestWay(Long id, List<String> exclude);
}
