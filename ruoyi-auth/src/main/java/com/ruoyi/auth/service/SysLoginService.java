package com.ruoyi.auth.service;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.ruoyi.auth.form.RegisterBody;
import com.ruoyi.auth.properties.UserPasswordProperties;
import com.ruoyi.common.core.constant.CacheConstants;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.constant.TenantConstants;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.common.core.enums.*;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.exception.user.CaptchaExpireException;
import com.ruoyi.common.core.exception.user.UserException;
import com.ruoyi.common.core.utils.*;
import com.ruoyi.common.core.utils.ip.AddressUtils;
import com.ruoyi.common.log.event.LogininforEvent;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.common.tenant.exception.TenantException;
import com.ruoyi.common.tenant.tenant.TenantHelper;
import com.ruoyi.system.api.RemoteLogService;
import com.ruoyi.system.api.RemoteTenantService;
import com.ruoyi.system.api.RemoteUserService;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.model.LoginUser;
import com.ruoyi.system.api.model.XcxLoginUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 登录校验方法
 *
 * @author ruoyi
 */
@Service
@Slf4j
public class SysLoginService {

    @DubboReference
    private RemoteLogService remoteLogService;
    @DubboReference
    private RemoteUserService remoteUserService;

    @DubboReference
    private RemoteTenantService remoteTenantService;

    @Autowired
    private UserPasswordProperties userPasswordProperties;

    /**
     * 登录
     */
    public String login(String username, String password, Long code, Long tenantId) {
        LoginUser userInfo = remoteUserService.getUserInfo(username, tenantId);
        if (userInfo.getStatus().equals(TenantStatus.CLOSE.getCode())) {
            throw new ServiceException("账号已关闭,请联系管理员!");
        }

        checkLogin(LoginType.PASSWORD, username, () -> !BCrypt.checkpw(password, userInfo.getPassword()));

        //验证商户是否开启谷歌验证
        if (userInfo.getTenantId() > 1) {
            TenantDto tenant = remoteTenantService.getTenant(userInfo.getTenantId());
            if (tenant.getId() != 0L && tenant.getStatus().equals(TenantStatus.CLOSE.getCode())) {
                throw new ServiceException("商户已停用,请联系管理员!");
            }
            String clientIP = ServletUtils.getClientIP();
            if (tenant.getOpenWhiteList().equals(OpenStatus.OPEN.getCode())) {
                if (ObjectUtil.isEmpty(tenant.getWhiteList())) {
                    throw new ServiceException("商户需要设置白名单,请联系管理员!");
                }
                Optional<String> any = Arrays.stream(tenant.getWhiteList().split(",")).filter(p -> p.equals(clientIP)).findAny();
                if (!any.isPresent()) {
                    throw new ServiceException("商户已设置登录白名单,请联系管理员!");
                }

            }

            if (tenant.getGoogleCaptcha()) {
                if (ObjectUtil.isNotEmpty(userInfo.getGoogleSecret())) {
                    if (ObjectUtil.isEmpty(code)) {
                        throw new ServiceException("已绑定谷歌验证码,请输入!");
                    }
                    //验证谷歌
                    boolean isTrue = GoogleAuthenticator.check_code(userInfo.getGoogleSecret(), code, System.currentTimeMillis());
                    if (!isTrue) {
                        throw new ServiceException("谷歌验证码错误");
                    }
                }
            }
            userInfo.setTenantName(tenant.getName());
        }
        SaLoginModel model = new SaLoginModel();
        model.setDevice(DeviceType.PC.getDevice());

        // 获取登录token
        LoginHelper.login(userInfo, model);

        recordLogininfor(userInfo, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
        return StpUtil.getTokenValue();
    }

    public String smsLogin(String phonenumber, String smsCode) {
        // 通过手机号查找用户
        LoginUser userInfo = remoteUserService.getUserInfoByPhonenumber(phonenumber);

        checkLogin(LoginType.SMS, userInfo.getUsername(), () -> !validateSmsCode(phonenumber, smsCode));
        SaLoginModel model = new SaLoginModel();
        model.setDevice(DeviceType.APP.getDevice());
        // 生成token
        LoginHelper.login(userInfo, model);

        recordLogininfor(userInfo, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
        return StpUtil.getTokenValue();
    }

    public String emailLogin(String email, String emailCode) {
        // 通过邮箱查找用户
        LoginUser userInfo = remoteUserService.getUserInfoByEmail(email);

        checkLogin(LoginType.EMAIL, userInfo.getUsername(), () -> !validateEmailCode(email, emailCode));
        SaLoginModel model = new SaLoginModel();
        model.setDevice(DeviceType.APP.getDevice());
        // 生成token
        LoginHelper.login(userInfo, model);

        recordLogininfor(userInfo, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
        return StpUtil.getTokenValue();
    }

    public String xcxLogin(String xcxCode) {
        // xcxCode 为 小程序调用 wx.login 授权后获取
        // todo 自行实现 校验 appid + appsrcret + xcxCode 调用登录凭证校验接口 获取 session_key 与 openid
        String openid = "";
        XcxLoginUser userInfo = remoteUserService.getUserInfoByOpenid(openid);
        SaLoginModel model = new SaLoginModel();
        model.setDevice(DeviceType.XCX.getDevice());
        // 生成token
        LoginHelper.login(userInfo, model);

        recordLogininfor(userInfo, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
        return StpUtil.getTokenValue();
    }

    /**
     * 退出登录
     */
    public void logout() {
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            if (ObjectUtil.isNotEmpty(loginUser)) {
                recordLogininfor(loginUser, Constants.LOGOUT, MessageUtils.message("user.logout.success"));
            }
        } catch (NotLoginException ignored) {
        } finally {
            try {
                StpUtil.logout();
            } catch (NotLoginException ignored) {
            }
        }
    }

    /**
     * 注册
     */
    public void register(RegisterBody registerBody) {
        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        // 校验用户类型是否存在
        String userType = UserType.getUserType(registerBody.getUserType()).getUserType();

        // 注册用户信息
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);
        sysUser.setNickName(username);
        sysUser.setPassword(BCrypt.hashpw(password));
        sysUser.setUserType(userType);
        boolean regFlag = remoteUserService.registerUserInfo(sysUser);
        if (!regFlag) {
            throw new UserException("user.register.error");
        }
        //recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success"));
    }

    /**
     * 记录登录信息
     *
     * @param user 用户名
     * @param status   状态
     * @param message  消息内容
     * @return
     */
    public void recordLogininfor(LoginUser user, String status, String message) {
        HttpServletRequest request = ServletUtils.getRequest();
        final UserAgent userAgent = UserAgentUtil.parse(request.getHeader("User-Agent"));
        final String ip = ServletUtils.getClientIP(request);

        String address = AddressUtils.getRealAddressByIP(ip);
        // 获取客户端操作系统
        String os = userAgent.getOs().getName();
        // 获取客户端浏览器
        String browser = userAgent.getBrowser().getName();
        // 封装对象
        LogininforEvent logininfor = new LogininforEvent();
        logininfor.setUserName(user.getUsername());
        logininfor.setIpaddr(ip);
        logininfor.setLoginLocation(address);
        logininfor.setBrowser(browser);
        logininfor.setOs(os);
        logininfor.setMsg(message);
        logininfor.setTenantId(user.getTenantId());
        logininfor.setTenantName(user.getTenantName());

        // 日志状态
        if (StringUtils.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
            logininfor.setStatus(Constants.LOGIN_SUCCESS_STATUS);
        } else if (Constants.LOGIN_FAIL.equals(status)) {
            logininfor.setStatus(Constants.LOGIN_FAIL_STATUS);
        }
        SpringUtils.context().publishEvent(logininfor);
    }

    /**
     * 校验短信验证码
     */
    private boolean validateSmsCode(String phonenumber, String smsCode) {
        String code = RedisUtils.getCacheObject(CacheConstants.CAPTCHA_CODE_KEY + phonenumber);
        if (StringUtils.isBlank(code)) {
            // recordLogininfor(phonenumber, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        return code.equals(smsCode);
    }

    /**
     * 校验邮箱验证码
     */
    private boolean validateEmailCode(String email, String emailCode) {
        String code = RedisUtils.getCacheObject(CacheConstants.CAPTCHA_CODE_KEY + email);
        if (StringUtils.isBlank(code)) {
            // recordLogininfor(email, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        return code.equals(emailCode);
    }

    /**
     * 登录校验
     */
    private void checkLogin(LoginType loginType, String username, Supplier<Boolean> supplier) {
        String errorKey = CacheConstants.PWD_ERR_CNT_KEY + username;
        String loginFail = Constants.LOGIN_FAIL;
        Integer maxRetryCount = userPasswordProperties.getMaxRetryCount();
        Integer lockTime = userPasswordProperties.getLockTime();

        // 获取用户登录错误次数(可自定义限制策略 例如: key + username + ip)
        Integer errorNumber = RedisUtils.getCacheObject(errorKey);
        // 锁定时间内登录 则踢出
        if (ObjectUtil.isNotNull(errorNumber) && errorNumber.equals(maxRetryCount)) {
            //recordLogininfor(username, loginFail, MessageUtils.message(loginType.getRetryLimitExceed(), maxRetryCount, lockTime));
            throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
        }

        if (supplier.get()) {
            // 是否第一次
            errorNumber = ObjectUtil.isNull(errorNumber) ? 1 : errorNumber + 1;
            // 达到规定错误次数 则锁定登录
            if (errorNumber.equals(maxRetryCount)) {
                RedisUtils.setCacheObject(errorKey, errorNumber, Duration.ofMinutes(lockTime));
                //recordLogininfor(username, loginFail, MessageUtils.message(loginType.getRetryLimitExceed(), maxRetryCount, lockTime));
                throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
            } else {
                // 未达到规定错误次数 则递增
                RedisUtils.setCacheObject(errorKey, errorNumber);
                //recordLogininfor(username, loginFail, MessageUtils.message(loginType.getRetryLimitCount(), errorNumber));
                throw new UserException(loginType.getRetryLimitCount(), errorNumber);
            }
        }
        // 登录成功 清空错误次数
        RedisUtils.deleteObject(errorKey);
    }

    /**
     * 校验租户
     *
     * @param tenantId 租户ID
     */
    public void checkTenant(Long tenantId) {
        if (!TenantHelper.isEnableDomain()) {
            return;
        }
        if (TenantConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            return;
        }
        TenantDto tenant = remoteTenantService.getTenant(tenantId);
        if (ObjectUtil.isNull(tenant)) {
            log.info("登录租户：{} 不存在.", tenantId);
            throw new TenantException("tenant.not.exists");
        }
        if (TenantStatus.CLOSE.getCode() == tenant.getStatus()) {
            log.info("登录租户：{} 已被停用.", tenantId);
            throw new TenantException("tenant.blocked");
        }
    }
}
