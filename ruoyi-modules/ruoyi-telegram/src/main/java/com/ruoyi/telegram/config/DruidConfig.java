package com.ruoyi.telegram.config;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DruidConfig {
    @PostConstruct
    public void setProperties() {
        System.setProperty("druid.mysql.usePingMethod", "false");
    }
}
