package com.ruoyi.telegram.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.updatesreceivers.ServerlessWebhook;

@Data
@Component
@Configuration
public class BotConfig {
    @Value("${bot.name}")
    private String name;
    @Value("${bot.token}")
    private String token;
    @Value("${bot.admin.name}")
    private String userName;
    @Value("${bot.admin.id}")
    private Long userId;
    @Value("${bot.webhookUrl}")
    private String webhookUrl;

    @Bean
    TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class, serverlessWebhook());
    }

    @Bean
    ServerlessWebhook serverlessWebhook() {
        return new ServerlessWebhook();
    }

}
