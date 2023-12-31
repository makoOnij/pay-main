package com.ruoyi.common.tenant.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.ruoyi.common.mybatis.config.MybatisPlusConfiguration;
import com.ruoyi.common.tenant.interceptor.TenantRequestInterceptor;
import com.ruoyi.common.tenant.properties.TenantProperties;
import com.ruoyi.common.tenant.tenant.TenantHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * 租户配置类
 */
@AutoConfiguration
@EnableConfigurationProperties(TenantProperties.class)
@ConditionalOnProperty(value = "tenant.enable", havingValue = "true")
public class TenantConfiguration {

    @Bean
    public ClientHttpRequestInterceptor TenantRequestInterceptor() {
        return new TenantRequestInterceptor();
    }

    @ConditionalOnBean(MybatisPlusConfiguration.class)
    @AutoConfiguration(after = {MybatisPlusConfiguration.class})
    static class MybatisPlusConfig {

        /**
         * 初始化租户配置
         */
        @Bean
        public boolean tenantInit(MybatisPlusInterceptor mybatisPlusInterceptor,
                                  TenantProperties tenantProperties) {
            List<InnerInterceptor> interceptors = new ArrayList<>();
            // 多租户插件 必须放到第一位
            interceptors.add(tenantLineInnerInterceptor(tenantProperties));
            interceptors.addAll(mybatisPlusInterceptor.getInterceptors());
            mybatisPlusInterceptor.setInterceptors(interceptors);
            return true;
        }

        /**
         * 多租户插件
         */
        public TenantLineInnerInterceptor tenantLineInnerInterceptor(TenantProperties tenantProperties) {
            return new TenantLineInnerInterceptor(new TenantHandler(tenantProperties));
        }
    }


}
