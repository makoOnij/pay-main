package com.ruoyi.boss.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ruoyi.boss.api.dto.DataReportDto;
import com.ruoyi.boss.domain.DataReport;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * 财务报Mapper接口
 *
 * @author ruoyi
 * @date 2023-11-26
 */
public interface DataReportMapper extends BaseMapperPlus<DataReportMapper, DataReport, DataReportDto> {

    Map<String, BigDecimal> selectTotal(@Param("ew") Wrapper<DataReport> queryWrapper);

    @Select({"select create_time,SUM(total_amount) as total_amount ,SUM(total_balance) as total_balance,SUM(total_charge) as total_charge,",
            "SUM(total_withdraw) as total_withdraw,SUM(total_payfor) as total_payfor,SUM(total_payfor_count) as total_payfor_count,SUM(total_order_count) as total_order_count, ",
            "SUM(total_fee) as total_fee,SUM(total_payfor_fee) as total_payfor_fee,SUM(total_order_amount) as total_order_amount,SUM(total_freeze) as total_freeze,SUM(total_payfor_rate) as total_payfor_rate",
            " from data_report ${ew.customSqlSegment}",
            " group by create_time"
    })
    @InterceptorIgnore(tenantLine = "true")
    List<DataReportDto> selectSumDataByDay(@Param("ew") Wrapper<DataReport> ew);
}
