package com.ruoyi.boss.dubbo;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.boss.api.RemoteBossService;
import com.ruoyi.boss.api.domain.PayWayDto;
import com.ruoyi.boss.api.domain.TenantWayDto;
import com.ruoyi.boss.api.dto.DataFlowDto;
import com.ruoyi.boss.api.dto.DataReportDto;
import com.ruoyi.boss.domain.PayChannel;
import com.ruoyi.boss.domain.TenantChannel;
import com.ruoyi.boss.mapper.PayChannelMapper;
import com.ruoyi.boss.mapper.TenantChannelMapper;
import com.ruoyi.boss.service.IDataFlowService;
import com.ruoyi.boss.service.IDataReportService;
import com.ruoyi.boss.service.ITenantChannelService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * 用户服务
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteBossServiceImpl implements RemoteBossService {


    private final IDataFlowService dataFlowService;
    private final IDataReportService dataReportService;
    private final PayChannelMapper payChannelMapper;
    private final TenantChannelMapper tenantChannelMapper;

    private final ITenantChannelService iTenantChannelService;


    @Override
    public TenantWayDto getWayByCode(String code) {
        TenantChannel tenantChannel = tenantChannelMapper.selectOne(Wrappers.<TenantChannel>lambdaQuery().eq(TenantChannel::getPayCode, code));

        return BeanUtil.toBean(tenantChannel, TenantWayDto.class);
    }

    @Override
    public PayWayDto queryParams(String tenantCode, String ifCode) {
        return payChannelMapper.selectVoOne(Wrappers.<PayChannel>lambdaQuery().eq(PayChannel::getCode, ifCode), PayWayDto.class);
    }

    @Override
    public List<TenantWayDto> getWayByTenantId(Long tenantId) {
        return tenantChannelMapper.selectVoList(Wrappers.<TenantChannel>lambdaQuery().eq(TenantChannel::getTenantId, tenantId), TenantWayDto.class);
    }

    @Override
    public List<TenantWayDto> getWayByTenantIds(List<Long> tenantIds) {
        return tenantChannelMapper.selectVoList(Wrappers.<TenantChannel>lambdaQuery().in(TenantChannel::getTenantId, tenantIds), TenantWayDto.class);
    }

    /**
     * 创建流水数据
     *
     * @param dataFlowDto
     */
    @Override
    public void insertFlow(List<DataFlowDto> dataFlowDto) {
        dataFlowService.batchInsert(dataFlowDto);
    }

    /**
     * 创建报表数据
     *
     * @param dataReportDto
     */
    @Override
    public void inertReport(DataReportDto dataReportDto) {
        dataReportService.insertData(dataReportDto);
    }

    @Override
    public void batchInsertOrUpdateTenantWay(List<TenantWayDto> collect) {
        tenantChannelMapper.duplicateKeyUpdate(BeanUtil.copyToList(collect, TenantChannel.class));
    }

    /**
     * 查询通道
     *
     * @param ids
     * @return
     */
    @Override
    public List<PayWayDto> getPayWayList(List<Long> ids) {
        return payChannelMapper.selectVoBatchIds(ids, PayWayDto.class);
    }

    /**
     * 清除商户通道
     *
     * @param id
     */
    @Override
    public void clearTenantWay(Long id) {
        tenantChannelMapper.delete(Wrappers.<TenantChannel>lambdaQuery().eq(TenantChannel::getTenantId, id));
    }

    /**
     * 查询商户当天报表
     *
     * @param tenantId
     * @param day
     * @return
     */
    @Override
    public List<DataReportDto> getReportByDay(Long tenantId, String day) {
        return dataReportService.getDatas(LocalDate.parse(day), Arrays.asList(tenantId));
    }

    /**
     * 配置最高权重通道
     *
     * @param id
     * @return
     */
    @Override
    public TenantWayDto getBestWay(Long id, List<String> exclude) {
        return iTenantChannelService.queryBestWay(id, exclude);
    }


}
