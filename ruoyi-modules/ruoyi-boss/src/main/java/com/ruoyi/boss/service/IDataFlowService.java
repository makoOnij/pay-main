package com.ruoyi.boss.service;

import com.ruoyi.boss.api.dto.DataFlowDto;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 资金明细Service接口
 *
 * @author ruoyi
 * @date 2023-11-26
 */
public interface IDataFlowService {

    /**
     * 查询资金明细
     */
    DataFlowDto queryById(Long id);

    /**
     * 查询资金明细列表
     */
    TableDataInfo<DataFlowDto> queryPageList(DataFlowDto bo, PageQuery pageQuery);

    /**
     * 查询资金明细列表
     */
    List<DataFlowDto> queryList(DataFlowDto bo);

    /**
     * 修改资金明细
     */
    Boolean insertByBo(DataFlowDto bo);

    /**
     * 修改资金明细
     */
    Boolean updateByBo(DataFlowDto bo);

    /**
     * 校验并批量删除资金明细信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    void batchInsert(List<DataFlowDto> dataFlowDto);
}
