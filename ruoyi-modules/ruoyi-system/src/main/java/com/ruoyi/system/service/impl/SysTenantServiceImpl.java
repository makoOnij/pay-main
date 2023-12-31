package com.ruoyi.system.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.boss.api.RemoteBossService;
import com.ruoyi.boss.api.domain.PayWayDto;
import com.ruoyi.boss.api.domain.TenantWayDto;
import com.ruoyi.boss.api.dto.DataFlowDto;
import com.ruoyi.boss.api.dto.DataReportDto;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.common.core.enums.AuditStatus;
import com.ruoyi.common.core.enums.Currency;
import com.ruoyi.common.core.enums.OpenStatus;
import com.ruoyi.common.core.enums.YesOrNoEnum;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.SeqKit;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.UsdtKit;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.common.tenant.annotation.IgnoreTenant;
import com.ruoyi.common.tenant.tenant.TenantBroker;
import com.ruoyi.common.tenant.tenant.TenantContextHolder;
import com.ruoyi.order.api.RemoteOrderService;
import com.ruoyi.order.api.dto.OrderChargeDto;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.model.LoginUser;
import com.ruoyi.system.domain.SysTenant;
import com.ruoyi.system.domain.bo.SysTenantBo;
import com.ruoyi.system.domain.bo.SysTenantChargeDto;
import com.ruoyi.system.domain.dto.BatchTenantDto;
import com.ruoyi.system.domain.dto.UpdateTenantSecurityDto;
import com.ruoyi.system.domain.vo.SysTenantVo;
import com.ruoyi.system.mapper.SysTenantMapper;
import com.ruoyi.system.service.ISysTenantService;
import com.ruoyi.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 租户Service业务层处理
 *
 * @author ruoyi
 * @date 2023-11-20
 */
@RequiredArgsConstructor
@Service
public class SysTenantServiceImpl implements ISysTenantService {

    private final SysTenantMapper baseMapper;

    private final ISysUserService sysUserService;

    @DubboReference
    private RemoteOrderService remoteOrderService;

    @DubboReference
    private RemoteBossService remoteBossService;
    /**
     * 查询租户
     */
    @Override
    public SysTenantVo queryById(Long id){
        SysTenantVo sysTenantVo = baseMapper.selectVoById(id);
        /**
         * 查询配置的通道
         */
        List<TenantWayDto> list = remoteBossService.getWayByTenantId(TenantContextHolder.getTenantId());
        sysTenantVo.setTenantWays(list);
        return sysTenantVo;
    }

    /**
     * 查询租户列表
     */
    @Override
    public TableDataInfo<SysTenantVo> queryPageList(SysTenantBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysTenant> lqw = buildQueryWrapper(bo);
        Page<SysTenantVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        if (!result.getRecords().isEmpty()) {
            //查询用户ID
            List<String> userNames = result.getRecords().stream().map(SysTenantVo::getLoginUser).collect(Collectors.toList());

            List<SysUser> sysUsers = sysUserService.selectUserByNames(userNames);
            result.getRecords().forEach(item -> {
                Optional<SysUser> first = sysUsers.stream().filter(p -> p.getUserName().equals(item.getLoginUser())).findFirst();
                first.ifPresent(item::setUser);
            });
            //加载代付列表
            List<Long> tenantIds = result.getRecords().stream().map(SysTenantVo::getId).collect(Collectors.toList());
            List<TenantWayDto> tenants = remoteBossService.getWayByTenantIds(tenantIds).stream().filter(p -> p.getStatus().equals(OpenStatus.OPEN.getCode())).collect(Collectors.toList());
            result.getRecords().forEach(item -> {
                List<TenantWayDto> collect = tenants.stream().filter(p -> p.getTenantId().equals(item.getId())).collect(Collectors.toList());
                item.setTenantWays(collect);
            });
        }
        return TableDataInfo.build(result);
    }

    /**
     * 查询租户列表
     */
    @Override
    public List<SysTenantVo> queryList(SysTenantBo bo) {
        LambdaQueryWrapper<SysTenant> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysTenant> buildQueryWrapper(SysTenantBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysTenant> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getName()), SysTenant::getTenantName, bo.getName());
        lqw.eq(bo.getExpireTime() != null, SysTenant::getExpireTime, bo.getExpireTime());
        lqw.eq(ObjectUtil.isNotEmpty(bo.getStatus()), SysTenant::getStatus, bo.getStatus());
        lqw.eq(bo.getTenantType() != null, SysTenant::getTenantType, bo.getTenantType());

        return lqw;
    }

    /**
     * 新增租户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(SysTenantBo bo) {
        if (ObjectUtil.isEmpty(bo.getSecret())) {
            bo.setSecret(SeqKit.genMerchantSecret());
        }
        boolean exists = baseMapper.exists(Wrappers.<SysTenant>query().eq("name", bo.getName()));
        if (exists) {
            throw new ServiceException("商户名称存在");
        }
        SysUser sysUser = sysUserService.selectUserByUserName(bo.getLoginUser());
        if (ObjectUtil.isNotEmpty(sysUser)) {
            throw new ServiceException("登录账号已存在");
        }
        SysTenant add = BeanUtil.toBean(bo, SysTenant.class);
        add.setCurrency(Currency.CNY.getCurrencyType());
        add.setCode(SeqKit.genMerchantCode());
        LoginUser loginUser = LoginHelper.getLoginUser();
        add.setTenantId(loginUser.getTenantId());
        add.setTenantName(loginUser.getTenantName());
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        //配置通道列表
        if (ObjectUtil.isNotEmpty(bo.getCheckedList())) {
            //查询通道信息
            List<PayWayDto> payWayList = remoteBossService.getPayWayList(bo.getCheckedList());
            List<TenantWayDto> collect = bo.getCheckedList().stream().map(p -> {
                TenantWayDto tenantWayDto = new TenantWayDto();
                tenantWayDto.setTenantId(add.getId());
                tenantWayDto.setTenantName(add.getName());
                tenantWayDto.setAmount(BigDecimal.ZERO);
                tenantWayDto.setStatus(OpenStatus.OPEN.getCode());
                tenantWayDto.setCurrency(Currency.CNY.getCurrencyType());
                tenantWayDto.setFee(BigDecimal.ZERO);
                tenantWayDto.setName(add.getName() + ":通道");
                tenantWayDto.setWayType(0);

                Optional<PayWayDto> first = payWayList.stream().filter(m -> m.getId().equals(p)).findFirst();
                first.ifPresent(way -> {
                    tenantWayDto.setMaxAmount(way.getMaxAmount());
                    tenantWayDto.setMinAmount(way.getMinAmount());
                    tenantWayDto.setPayCode(way.getCode());
                    tenantWayDto.setPayName(way.getName());
                    tenantWayDto.setRate(way.getRate());
                    tenantWayDto.setPayId(way.getId());

                });
                tenantWayDto.setFixedAmount(BigDecimal.ZERO);

                return tenantWayDto;
            }).collect(Collectors.toList());
            remoteBossService.batchInsertOrUpdateTenantWay(collect);
        }
        TenantBroker.runAs(add.getId(),(id)->{
            //创建商户登录账号
            SysUser user=new SysUser();
            user.setUserName(add.getLoginUser());
            user.setNickName(add.getLoginUser());
            user.setPassword(BCrypt.hashpw(add.getLoginPassword()));
            user.setRoleIds(new Long[]{bo.getTenantType().longValue()});
            user.setTenantId(add.getId());
            user.setTenantName(add.getTenantName());
            sysUserService.insertUser(user);
        });

        return flag;
    }

    /**
     * 修改租户
     */
    @Override
    public Boolean updateByBo(SysTenantBo bo) {
        SysTenant update = BeanUtil.toBean(bo, SysTenant.class);

        //配置通道列表
        if (ObjectUtil.isNotEmpty(bo.getCheckedList())) {
            //查询通道信息
            List<PayWayDto> payWayList = remoteBossService.getPayWayList(bo.getCheckedList());
            List<TenantWayDto> collect = bo.getCheckedList().stream().map(p -> {
                TenantWayDto tenantWayDto = new TenantWayDto();
                tenantWayDto.setTenantId(update.getId());
                tenantWayDto.setTenantName(update.getName());
                tenantWayDto.setAmount(BigDecimal.ZERO);
                tenantWayDto.setStatus(OpenStatus.OPEN.getCode());
                tenantWayDto.setCurrency(Currency.CNY.getCurrencyType());
                tenantWayDto.setFee(BigDecimal.ZERO);
                tenantWayDto.setName(update.getName() + ":通道");
                tenantWayDto.setWayType(0);

                Optional<PayWayDto> first = payWayList.stream().filter(m -> m.getId().equals(p)).findFirst();
                first.ifPresent(way -> {
                    tenantWayDto.setMaxAmount(way.getMaxAmount());
                    tenantWayDto.setMinAmount(way.getMinAmount());
                    tenantWayDto.setPayCode(way.getCode());
                    tenantWayDto.setPayName(way.getName());
                    tenantWayDto.setRate(way.getRate());
                    tenantWayDto.setPayId(way.getId());

                });
                tenantWayDto.setFixedAmount(BigDecimal.ZERO);
                return tenantWayDto;
            }).collect(Collectors.toList());
            remoteBossService.batchInsertOrUpdateTenantWay(collect);
        } else {
            remoteBossService.clearTenantWay(update.getId());
        }
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysTenant entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除租户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        baseMapper.deleteBatchIds(ids);
        sysUserService.deleteUserByTenantIds(ids);
        return true;
    }

    /**
     * 商户充值
     *
     * @param bo
     * @return
     */
    @Override
    public int amountIncrease(SysTenantChargeDto bo) {
        TenantDto tenant = baseMapper.selectVoById(bo.getId(), TenantDto.class);
        if (ObjectUtil.isEmpty(tenant)) {
            throw new ServiceException("商户不存在");
        }
        OrderChargeDto orderCharge = new OrderChargeDto();
        orderCharge.setOrderNo(SeqKit.genMerchantChargeId());
        orderCharge.setTenantId(tenant.getTenantId());
        orderCharge.setTenantName(tenant.getTenantName());
        orderCharge.setAmount(bo.getAmount());
        orderCharge.setStatus(AuditStatus.SUCCESS.getCode());
        orderCharge.setParentName(tenant.getTenantName());
        orderCharge.setParentId(tenant.getTenantId());
        orderCharge.setAmount(bo.getAmount());
        if (tenant.getIsFloat().equals(YesOrNoEnum.YES.getCode())) {
            orderCharge.setRate(tenant.getChargeRate());
            orderCharge.setIsFloat(YesOrNoEnum.NO.getCode());
        } else {

            BigDecimal usdtRate = UsdtKit.getCurrentRate();
            orderCharge.setRate(usdtRate.subtract(tenant.getFloatDown()));
            orderCharge.setIsFloat(YesOrNoEnum.YES.getCode());
        }
        orderCharge.setRealAmount(orderCharge.getAmount().multiply(orderCharge.getRate()));
        orderCharge.setRemark("渠道充值,无需审核");
       
        remoteOrderService.addOrder(orderCharge);
        remoteBossService.insertFlow(DataFlowDto.buildCharge(tenant, orderCharge.getRealAmount()));
        remoteBossService.inertReport(DataReportDto.buildCharge(tenant, orderCharge.getRealAmount(), BigDecimal.ZERO));
        baseMapper.update(Wrappers.<SysTenant>update().setSql("balance=balance+{0}", orderCharge.getRealAmount()).eq("id", bo.getId()));
        return 1;
    }

    /**
     * 修改租户状态
     *
     * @param user
     * @return
     */
    @Override
    public int updateStatus(SysTenant user) {
        return baseMapper.update(Wrappers.<SysTenant>update().set("status", user.getStatus()).eq("id", user.getId()));
    }

    /**
     * 批量更新充值汇率
     *
     * @param ids
     * @param whiteList
     * @return
     */
    @Override
    public int batchUpdateWhiteList(List<Long> ids, String whiteList) {
        return baseMapper.update(Wrappers.<SysTenant>update().set("white_list", whiteList).in("id", ids));
    }

    /**
     * 批量更新状态
     *
     * @param ids
     * @param status
     * @return
     */
    @Override
    public int batchUpdateStatus(List<Long> ids, Integer status) {
        return baseMapper.update(Wrappers.<SysTenant>update().set("status", status).in("id", ids));
    }

    /**
     * 批量修改内冲汇率
     *
     * @return
     */
    @Override
    public int batchUpdateChargeRate(BatchTenantDto dto) {
        return baseMapper.update(Wrappers.<SysTenant>update().set("charge_rate", dto.getChargeRate())
                .set("float_up", dto.getFloatUp())
                .set("float_down", dto.getFloatDown())
                .in("id", dto.getIds()));
    }

    /**
     * 批量更新接口白名单
     *
     * @param ids
     * @param apiWhiteList
     * @return
     */
    @Override
    public int batchUpdateApiWhiteList(List<Long> ids, String apiWhiteList) {
        return baseMapper.update(Wrappers.<SysTenant>update().set("api_white_list", apiWhiteList).in("id", ids));
    }

    /**
     * 修改商户安全信息
     *
     * @param bo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSecurity(UpdateTenantSecurityDto bo) {
        SysTenant tenant = baseMapper.selectById(bo.getId());
        if (tenant == null) {
            throw new ServiceException("商户不存在");
        }
        if (ObjectUtil.isNotEmpty(bo.getGoogleSecret())) {
            SysUser sysUser = sysUserService.selectUserByUserName(tenant.getLoginUser());
            if (sysUser == null) {
                throw new ServiceException("商户信息错误");
            }
            sysUserService.updateGoogle(sysUser.getUserId(), bo.getGoogleSecret());
        }
        return baseMapper.update(Wrappers.<SysTenant>update()
                .set("open_api_white_list", bo.getOpenApiWhiteList())
                .set("open_white_list", bo.getOpenWhiteList())
                .set(ObjectUtil.isNotEmpty(bo.getWhiteList()), "white_list", bo.getWhiteList())
                .set(ObjectUtil.isNotEmpty(bo.getApiWhiteList()), "api_white_list", bo.getApiWhiteList())
                .eq("id", bo.getId())
        );
    }

    /**
     * 更新自动代付
     *
     * @param bo
     * @return
     */
    @Override
    public int updateAutoPay(UpdateTenantSecurityDto bo) {
        SysTenant tenant = baseMapper.selectById(bo.getId());
        tenant.setAutoPay(bo.getAutoPay());
        return baseMapper.updateById(tenant);
    }

    @Override
    public List<TenantDto> getChilds(Long tenantId) {
        List<SysTenant> sysTenants = baseMapper.selectChildList(tenantId);
        return BeanUtil.copyToList(sysTenants, TenantDto.class);
    }

    @Override
    @IgnoreTenant
    public BigDecimal getChargeRate(Long tenantId) {
        SysTenant tenant = baseMapper.selectById(tenantId);
        if (ObjectUtil.isNotEmpty(tenant.getChargeRate()) && tenant.getChargeRate().compareTo(BigDecimal.ZERO) > 0) {
            return tenant.getChargeRate();
        }

        return UsdtKit.getCurrentRate().subtract(tenant.getFloatDown());
    }

    @Override
    @IgnoreTenant
    public BigDecimal getWithdrawRate(Long tenantId) {
        SysTenant tenant = baseMapper.selectById(tenantId);
        if (ObjectUtil.isNotEmpty(tenant.getChargeRate())) {
            return tenant.getChargeRate();
        }
        return UsdtKit.getCurrentRate().add(tenant.getFloatUp());
    }

    @Override
    @IgnoreTenant
    public String getChargeAddress(Long tenantId) {
        SysTenant tenant = baseMapper.selectById(tenantId);
        if (ObjectUtil.isNotEmpty(tenant)) {
            SysTenant tenant1 = baseMapper.selectById(tenant.getTenantId());
            if (ObjectUtil.isNotEmpty(tenant1)) {
                return tenant1.getChargeAddress();
            }
        }
        return "";
    }

    @Override
    @Lock4j(keys = {"#tenantId"}, expire = 60000, acquireTimeout = 1000)
    public void balanceRelease(Long tenantId, BigDecimal amount) {
        baseMapper.update(Wrappers.<SysTenant>update()
                .setSql("balance=balance-{0},freeze_balance=freeze_balance-{1}", amount, amount)
                .eq("id", tenantId));
    }

    @Override
    @Lock4j(keys = {"#tenantId"}, expire = 60000, acquireTimeout = 1000)
    public void balanceFreeze(Long tenantId, BigDecimal realAmount) {
        baseMapper.update(Wrappers.<SysTenant>update().setSql("freeze_balance=freeze_balance+{0}", realAmount).eq("id", tenantId));
    }

    @Override
    @Lock4j(keys = {"#tenantId"}, expire = 60000, acquireTimeout = 1000)
    public void balanceIncrease(Long tenantId, BigDecimal realAmount) {
        baseMapper.update(Wrappers.<SysTenant>update().setSql("balance=balance+{0}", realAmount).eq("id", tenantId));
    }

    @Override
    public int updateApi(SysTenantBo bo) {
        int update = baseMapper.update(Wrappers.<SysTenant>lambdaUpdate()
                .set(SysTenant::getCode, bo.getCode())
                .set(SysTenant::getSecret, bo.getSecret())
                .set(SysTenant::getOpenApiWhiteList, bo.getOpenApiWhiteList())
                .set(SysTenant::getOpenWhiteList, bo.getOpenWhiteList())
                .set(SysTenant::getWhiteList, bo.getWhiteList())
                .set(SysTenant::getApiWhiteList, bo.getApiWhiteList())
                .set(SysTenant::getChargeAddress, bo.getChargeAddress())
                .eq(SysTenant::getId, bo.getId())
        );
        return update;
    }

    @Override
    @Lock4j(keys = {"#tenantId"}, expire = 60000, acquireTimeout = 1000)
    public void balanceReduce(Long tenantId, BigDecimal realAmount) {
        baseMapper.update(Wrappers.<SysTenant>update().setSql("balance=balance-{}", realAmount).eq("id", tenantId));
    }

    @Override
    public TenantDto getByDomain(String host) {

        TenantDto tenantDto = baseMapper.selectVoOne(Wrappers.<SysTenant>lambdaQuery().eq(SysTenant::getMerchantDomain, host).last("limit 1"), TenantDto.class);
        if (ObjectUtil.isEmpty(tenantDto)) {
            tenantDto = baseMapper.selectVoById(1L, TenantDto.class);
        }
        return tenantDto;
    }
}
