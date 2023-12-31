package com.ruoyi.auth.controller;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.auth.domain.LoginTenantVo;
import com.ruoyi.auth.domain.TenantVo;
import com.ruoyi.auth.form.EmailLoginBody;
import com.ruoyi.auth.form.LoginBody;
import com.ruoyi.auth.form.RegisterBody;
import com.ruoyi.auth.form.SmsLoginBody;
import com.ruoyi.auth.service.SysLoginService;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.tenant.tenant.TenantHelper;
import com.ruoyi.system.api.RemoteTenantService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * token 控制
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
public class TokenController {

    private final SysLoginService sysLoginService;

    @DubboReference
    private RemoteTenantService remoteTenantService;

    /**
     * 登录方法
     */
    @PostMapping("login")
    public R<Map<String, Object>> login(@Validated @RequestBody LoginBody form) {
        if (ObjectUtil.isNotEmpty(form.getTenantId())) {
            sysLoginService.checkTenant(form.getTenantId());
        }
        // 用户登录
        String accessToken = sysLoginService.login(form.getUsername(), form.getPassword(), form.getGoogleCode(), form.getTenantId());

        // 接口返回信息
        Map<String, Object> rspMap = new HashMap<>();
        rspMap.put(Constants.ACCESS_TOKEN, accessToken);
        return R.ok(rspMap);
    }

    /**
     * 短信登录
     *
     * @param smsLoginBody 登录信息
     * @return 结果
     */
    @PostMapping("/smsLogin")
    public R<Map<String, Object>> smsLogin(@Validated @RequestBody SmsLoginBody smsLoginBody) {
        Map<String, Object> ajax = new HashMap<>();
        // 生成令牌
        String token = sysLoginService.smsLogin(smsLoginBody.getPhonenumber(), smsLoginBody.getSmsCode());
        ajax.put(Constants.ACCESS_TOKEN, token);
        return R.ok(ajax);
    }

    /**
     * 邮件登录
     *
     * @param body 登录信息
     * @return 结果
     */
    @PostMapping("/emailLogin")
    public R<Map<String, Object>> emailLogin(@Validated @RequestBody EmailLoginBody body) {
        Map<String, Object> ajax = new HashMap<>();
        // 生成令牌
        String token = sysLoginService.emailLogin(body.getEmail(), body.getEmailCode());
        ajax.put(Constants.ACCESS_TOKEN, token);
        return R.ok(ajax);
    }

    /**
     * 小程序登录(示例)
     *
     * @param xcxCode 小程序code
     * @return 结果
     */
    @PostMapping("/xcxLogin")
    public R<Map<String, Object>> xcxLogin(@NotBlank(message = "{xcx.code.not.blank}") String xcxCode) {
        Map<String, Object> ajax = new HashMap<>();
        // 生成令牌
        String token = sysLoginService.xcxLogin(xcxCode);
        ajax.put(Constants.ACCESS_TOKEN, token);
        return R.ok(ajax);
    }

    /**
     * 登出方法
     */
    @DeleteMapping("logout")
    public R<Void> logout() {
        sysLoginService.logout();
        return R.ok();
    }

    /**
     * 用户注册
     */
    @PostMapping("register")
    public R<Void> register(@RequestBody RegisterBody registerBody) {
        // 用户注册
        sysLoginService.register(registerBody);
        return R.ok();
    }

    @GetMapping("/tenant/list")
    public R<LoginTenantVo> tenantList(HttpServletRequest request) throws Exception {
        // 获取域名
        String host;
        String referer = request.getHeader("referer");
        if (StringUtils.isNotBlank(referer)) {
            // 这里从referer中取值是为了本地使用hosts添加虚拟域名，方便本地环境调试
            host = referer.split("//")[1].split("/")[0];
        } else {
            host = new URL(request.getRequestURL().toString()).getHost();
        }
        TenantDto currentTenant = remoteTenantService.getTenantByDomain(host);
        // 返回对象
        LoginTenantVo vo = new LoginTenantVo();
        vo.setData(BeanCopyUtils.copy(currentTenant, TenantVo.class));
        vo.setTenantEnabled(TenantHelper.isEnableDomain());
        return R.ok(vo);
    }

}
