package com.ruoyi.mq;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * SpringCloud-Stream-MQ 案例项目
 *
 * @author Lion Li
 */
@EnableDubbo
@SpringBootApplication
@ConfigurationPropertiesScan
public class RuoYiMqApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RuoYiMqApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  MQ案例模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }

}
