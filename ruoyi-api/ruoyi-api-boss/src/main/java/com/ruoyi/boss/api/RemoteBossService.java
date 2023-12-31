package com.ruoyi.boss.api;

import com.ruoyi.boss.api.domain.PayWayDto;
import com.ruoyi.boss.api.domain.TenantWayDto;
import com.ruoyi.boss.api.dto.DataFlowDto;
import com.ruoyi.boss.api.dto.DataReportDto;

import java.util.List;

/**
 * 订单服务
 *
 * @author Lion Li
 */
public interface RemoteBossService {

    TenantWayDto getWayByCode(String code);

    PayWayDto queryParams(String tenantCode, String ifCode);

    List<TenantWayDto> getWayByTenantId(Long tenantId);

    List<TenantWayDto> getWayByTenantIds(List<Long> tenantIds);

    /**
     * 创建流水数据
     *
     * @param dataFlowDto
     */
    void insertFlow(List<DataFlowDto> dataFlowDto);

    /**
     * 创建报表数据
     *
     * @param dataReportDto
     */
    void inertReport(DataReportDto dataReportDto);

    void batchInsertOrUpdateTenantWay(List<TenantWayDto> collect);

    /**
     * 查询通道
     *
     * @param ids
     * @return
     */
    List<PayWayDto> getPayWayList(List<Long> ids);

    /**
     * 清除商户通道
     *
     * @param id
     */
    void clearTenantWay(Long id);

    /**
     * 查询商户当天报表
     *
     * @param tenantId
     * @param day
     * @return
     */
    List<DataReportDto> getReportByDay(Long tenantId, String day);

    /**
     * 配置最高权重通道
     *
     * @param id
     * @return
     */
    TenantWayDto getBestWay(Long id, List<String> exclude);
}
