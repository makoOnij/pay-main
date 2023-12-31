package com.ruoyi.boss.job;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.boss.api.dto.DataReportDto;
import com.ruoyi.boss.domain.DataReport;
import com.ruoyi.boss.mapper.DataReportMapper;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.common.core.enums.TenantType;
import com.ruoyi.system.api.RemoteTenantService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReportTaskService {

    @Autowired
    private DataReportMapper dataReportMapper;

    @DubboReference
    private RemoteTenantService remoteTenantService;

    /**
     * 每天定时插入商户报表数据
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void task() {
        List<TenantDto> allTenant = remoteTenantService.getAllTenant();
        List<DataReportDto> collect = allTenant.stream().filter(p -> p.getTenantType().equals(TenantType.MERCHANT.getCode())).map(p -> {
            DataReportDto report = DataReportDto.buildEmpty();
            report.setTenantId(p.getId());
            report.setTenantName(p.getName());
            return report;

        }).collect(Collectors.toList());

        dataReportMapper.insertBatch(BeanUtil.copyToList(collect, DataReport.class));
    }
}
