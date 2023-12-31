package com.ruoyi.boss.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ruoyi.boss.api.dto.DataFlowDto;
import com.ruoyi.boss.domain.DataFlow;
import com.ruoyi.boss.domain.dto.FlowTotalDto;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 资金明细Mapper接口
 *
 * @author ruoyi
 * @date 2023-11-26
 */
public interface DataFlowMapper extends BaseMapperPlus<DataFlowMapper, DataFlow, DataFlowDto> {

    @Select("select IFNULL(SUM(amount),0) as amountTotal, IFNULL(COUNT(*),0) as countTotal,IFNULL(SUM(fee),0) as feeTotal from data_flow ${ew.customSqlSegment}")
    FlowTotalDto selectTotal(@Param("ew") Wrapper<DataFlow> queryWrapper);
}
