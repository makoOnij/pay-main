package com.ruoyi.telegram;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.telegram.config.BotConfig;
import com.ruoyi.telegram.impl.MessagePollingBot;
import com.ruoyi.telegram.impl.MessageWebhookBot;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.scheduling.annotation.EnableAsync;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

/**
 * 纸飞机对接模块
 */
@EnableDubbo
@SpringBootApplication
@EnableAsync
@RequiredArgsConstructor
public class RuoYiTelegramApplication implements CommandLineRunner {

    private final TelegramBotsApi telegramBotsApi;
    private final BotConfig botConfig;
    private final MessagePollingBot messagePollingBot;
    private final MessageWebhookBot messageWebhookBot;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RuoYiTelegramApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  纸飞机模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }

    @Override
    public void run(String... args) throws Exception {
        SetWebhook setWebhook = SetWebhook.builder().url(botConfig.getWebhookUrl()).build();
        if (ObjectUtil.isEmpty(botConfig.getWebhookUrl())) {
            telegramBotsApi.registerBot(messagePollingBot);
        } else {
            telegramBotsApi.registerBot(messageWebhookBot, setWebhook);
        }
    }
}
