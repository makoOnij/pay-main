package com.ruoyi.boss.service;

import com.ruoyi.boss.api.dto.DataReportDto;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * 财务报Service接口
 *
 * @author ruoyi
 * @date 2023-11-26
 */
public interface IDataReportService {

    /**
     * 查询财务报
     */
    DataReportDto queryById(Long id);

    /**
     * 查询财务报列表
     */
    TableDataInfo<DataReportDto> queryPageList(DataReportDto bo, PageQuery pageQuery);

    /**
     * 查询财务报列表
     */
    List<DataReportDto> queryList(DataReportDto bo);

    /**
     * 修改财务报
     */
    Boolean insertByBo(DataReportDto bo);

    /**
     * 修改财务报
     */
    Boolean updateByBo(DataReportDto bo);

    /**
     * 校验并批量删除财务报信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    void insertData(DataReportDto dataReportDto);

    DataReportDto getData(LocalDate time, List<Long> ids);

    List<DataReportDto> getDatas(LocalDate localDate, List<Long> ids);
}
