package com.ruoyi.boss.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.boss.api.dto.DataReportDto;
import com.ruoyi.boss.domain.DataReport;
import com.ruoyi.boss.mapper.DataReportMapper;
import com.ruoyi.boss.service.IDataReportService;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 财务报Service业务层处理
 *
 * @author ruoyi
 * @date 2023-11-26
 */
@RequiredArgsConstructor
@Service
public class DataReportServiceImpl implements IDataReportService {

    private final DataReportMapper baseMapper;

    /**
     * 查询财务报
     */
    @Override
    public DataReportDto queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询财务报列表
     */
    @Override
    public TableDataInfo<DataReportDto> queryPageList(DataReportDto bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DataReport> lqw = buildQueryWrapper(bo);
        Page<DataReportDto> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询财务报列表
     */
    @Override
    public List<DataReportDto> queryList(DataReportDto bo) {
        LambdaQueryWrapper<DataReport> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DataReport> buildQueryWrapper(DataReportDto bo) {

        LambdaQueryWrapper<DataReport> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getTenantName()), DataReport::getTenantName, bo.getTenantName());
        lqw.eq(bo.getTenantId() != null, DataReport::getTenantId, bo.getTenantId());
        lqw.eq(bo.getTotalAmount() != null, DataReport::getTotalAmount, bo.getTotalAmount());
        lqw.eq(bo.getTotalBalance() != null, DataReport::getTotalBalance, bo.getTotalBalance());
        lqw.eq(bo.getTotalFreeze() != null, DataReport::getTotalFreeze, bo.getTotalFreeze());
        lqw.eq(bo.getTotalCharge() != null, DataReport::getTotalCharge, bo.getTotalCharge());
        lqw.eq(bo.getTotalWithdraw() != null, DataReport::getTotalWithdraw, bo.getTotalWithdraw());
        lqw.eq(bo.getTotalPayfor() != null, DataReport::getTotalPayfor, bo.getTotalPayfor());
        lqw.eq(bo.getTotalOrderCount() != null, DataReport::getTotalOrderCount, bo.getTotalOrderCount());
        lqw.eq(bo.getTotalFee() != null, DataReport::getTotalFee, bo.getTotalFee());
        lqw.eq(bo.getTotalPayforRate() != null, DataReport::getTotalPayforRate, bo.getTotalPayforRate());
        lqw.eq(bo.getTotalPayforFee() != null, DataReport::getTotalPayforFee, bo.getTotalPayforFee());

        lqw.in(ObjectUtil.isNotEmpty(bo.getIds()), DataReport::getTenantId, bo.getIds());
        lqw.orderByDesc(DataReport::getCreateTime);
        return lqw;
    }

    /**
     * 新增财务报
     */
    @Override
    public Boolean insertByBo(DataReportDto bo) {
        DataReport add = BeanUtil.toBean(bo, DataReport.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改财务报
     */
    @Override
    public Boolean updateByBo(DataReportDto bo) {
        DataReport update = BeanUtil.toBean(bo, DataReport.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DataReport entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除财务报
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public void insertData(DataReportDto dataReportDto) {

        baseMapper.duplicateKeyAdd(Collections.singletonList(BeanUtil.toBean(dataReportDto, DataReport.class)));
    }

    @Override
    public DataReportDto getData(LocalDate time, List<Long> ids) {


        List<DataReportDto> dataReportDtos = baseMapper.selectSumDataByDay(Wrappers.<DataReport>lambdaQuery()
                .eq(DataReport::getCreateTime, time)
                .in(!ids.isEmpty(), DataReport::getTenantId, ids));
        if (!dataReportDtos.isEmpty()) {
            return dataReportDtos.get(0);
        }

        return null;

    }

    @Override
    public List<DataReportDto> getDatas(LocalDate localDate, List<Long> ids) {

        return baseMapper.selectSumDataByDay(Wrappers.<DataReport>lambdaQuery()
                .gt(DataReport::getCreateTime, localDate).in(!ids.isEmpty(), DataReport::getTenantId, ids));
    }
}
