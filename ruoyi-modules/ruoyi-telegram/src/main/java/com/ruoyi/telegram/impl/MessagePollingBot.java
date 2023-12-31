package com.ruoyi.telegram.impl;

import com.ruoyi.telegram.command.HelpCommand;
import com.ruoyi.telegram.command.ReportCommand;
import com.ruoyi.telegram.config.BotConfig;
import com.ruoyi.telegram.handler.Emoji;
import com.ruoyi.telegram.service.MessageProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;


@Component
public class MessagePollingBot extends TelegramLongPollingCommandBot {

    @Autowired
    private MessageProcessService messageProcessService;

    @Autowired
    private BotConfig botConfig;


    @PostConstruct
    public void initData() {
        messageProcessService.initData();
        register(new ReportCommand());
        HelpCommand helpCommand = new HelpCommand(this);
        register(helpCommand);

        registerDefaultAction((absSender, message) -> {
            SendMessage commandUnknownMessage = new SendMessage();
            commandUnknownMessage.setChatId(message.getChatId());
            commandUnknownMessage.setText("The command '" + message.getText() + "' is not known by this bot. Here comes some help " + Emoji.AMBULANCE);
            try {
                absSender.execute(commandUnknownMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            helpCommand.execute(absSender, message.getFrom(), message.getChat(), new String[]{});
        });

    }
    /**
     * Return username of this bot
     */
    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }


    @Override
    public void processNonCommandUpdate(Update update) {

        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                messageProcessService.process(update, this);
            }
        }

    }
}