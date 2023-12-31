package com.ruoyi.common.tenant.aspect;

import com.ruoyi.common.tenant.tenant.TenantHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@AutoConfiguration
public class NoTenantAspect {


    /**
     * 切入点
     */
    @Pointcut("@annotation(com.ruoyi.common.tenant.annotation.IgnoreTenant)")
    public void pointcut() {

    }

    /**
     * 环绕操作
     *
     * @param point 切入点
     * @return 原方法返回值
     * @throws Throwable 异常信息
     */
    @Around(value = "pointcut()")
    public Object aroundLog(ProceedingJoinPoint point) {
        return TenantHelper.ignore(() -> {
            try {
                return point.proceed();
            } catch (Throwable e) {
                log.error("禁用租户执行异常:{}", e.getMessage());
            }
            return null;
        });

    }

}