package com.ruoyi.boss.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.boss.api.dto.DataFlowDto;
import com.ruoyi.boss.domain.DataFlow;
import com.ruoyi.boss.domain.dto.FlowTotalDto;
import com.ruoyi.boss.mapper.DataFlowMapper;
import com.ruoyi.boss.service.IDataFlowService;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 资金明细Service业务层处理
 *
 * @author ruoyi
 * @date 2023-11-26
 */
@RequiredArgsConstructor
@Service
public class DataFlowServiceImpl implements IDataFlowService {

    private final DataFlowMapper baseMapper;

    /**
     * 查询资金明细
     */
    @Override
    public DataFlowDto queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询资金明细列表
     */
    @Override
    public TableDataInfo<DataFlowDto> queryPageList(DataFlowDto bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DataFlow> lqw = buildQueryWrapper(bo);
        Page<DataFlowDto> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        FlowTotalDto statices = baseMapper.selectTotal(lqw);
        if (ObjectUtil.isEmpty(statices)) {
            statices = FlowTotalDto.buildEmpty();
        }
        return TableDataInfo.build(result, statices);
    }

    /**
     * 查询资金明细列表
     */
    @Override
    public List<DataFlowDto> queryList(DataFlowDto bo) {
        LambdaQueryWrapper<DataFlow> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DataFlow> buildQueryWrapper(DataFlowDto bo) {
        LambdaQueryWrapper<DataFlow> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserAmount() != null, DataFlow::getUserAmount, bo.getUserAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getUserAccount()), DataFlow::getUserAccount, bo.getUserAccount());
        lqw.eq(bo.getCurrencyAmount() != null, DataFlow::getCurrencyAmount, bo.getCurrencyAmount());
        lqw.eq(bo.getBeforeAmount() != null, DataFlow::getBeforeAmount, bo.getBeforeAmount());
        lqw.eq(bo.getAmount() != null, DataFlow::getAmount, bo.getAmount());
        lqw.eq(bo.getBeforeFreeAmount() != null, DataFlow::getBeforeFreeAmount, bo.getBeforeFreeAmount());
        lqw.eq(bo.getFreeAmount() != null, DataFlow::getFreeAmount, bo.getFreeAmount());
        lqw.eq(bo.getFee() != null, DataFlow::getFee, bo.getFee());
        lqw.eq(StringUtils.isNotBlank(bo.getOrderNo()), DataFlow::getOrderNo, bo.getOrderNo());
        lqw.eq(bo.getFlowType() != null, DataFlow::getFlowType, bo.getFlowType());
        lqw.eq(bo.getTenantId() != null, DataFlow::getTenantId, bo.getTenantId());
        lqw.like(StringUtils.isNotBlank(bo.getTenantName()), DataFlow::getTenantName, bo.getTenantName());
        lqw.in(ObjectUtil.isNotEmpty(bo.getTenantIds()), DataFlow::getTenantId, bo.getTenantIds());
        lqw.orderByDesc(DataFlow::getCreateTime);
        return lqw;
    }

    /**
     * 新增资金明细
     */
    @Override
    public Boolean insertByBo(DataFlowDto bo) {
        DataFlow add = BeanUtil.toBean(bo, DataFlow.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改资金明细
     */
    @Override
    public Boolean updateByBo(DataFlowDto bo) {
        DataFlow update = BeanUtil.toBean(bo, DataFlow.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DataFlow entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除资金明细
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public void batchInsert(List<DataFlowDto> dataFlowDto) {
        baseMapper.insertBatch(BeanUtil.copyToList(dataFlowDto, DataFlow.class));
    }
}
