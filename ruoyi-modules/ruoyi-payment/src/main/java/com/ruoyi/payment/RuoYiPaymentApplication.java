package com.ruoyi.payment;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 订单网关
 *
 * @author ruoyi
 */
@EnableDubbo
@SpringBootApplication
@EnableAsync
public class RuoYiPaymentApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RuoYiPaymentApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  代付网关模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
