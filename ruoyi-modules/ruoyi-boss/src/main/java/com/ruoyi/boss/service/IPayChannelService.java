package com.ruoyi.boss.service;

import com.ruoyi.boss.domain.bo.PayChannelBo;
import com.ruoyi.boss.domain.vo.PayChannelVo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 通道列表Service接口
 *
 * @author ruoyi
 * @date 2023-11-24
 */
public interface IPayChannelService {

    /**
     * 查询通道列表
     */
    PayChannelVo queryById(Long id);

    /**
     * 查询通道列表列表
     */
    TableDataInfo<PayChannelVo> queryPageList(PayChannelBo bo, PageQuery pageQuery);

    /**
     * 查询通道列表列表
     */
    List<PayChannelVo> queryList(PayChannelBo bo);

    /**
     * 修改通道列表
     */
    Boolean insertByBo(PayChannelBo bo);

    /**
     * 修改通道列表
     */
    Boolean updateByBo(PayChannelBo bo);

    /**
     * 校验并批量删除通道列表信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    Boolean updateStatusByIds(Collection<Long> ids, Integer status);

    /**
     * 更新状态
     *
     * @param id
     * @param status
     * @return
     */
    int updateStatus(Long id, Integer status);

    /**
     * 查询可用通道
     *
     * @return
     */
    List<PayChannelVo> queryValidList();
}
