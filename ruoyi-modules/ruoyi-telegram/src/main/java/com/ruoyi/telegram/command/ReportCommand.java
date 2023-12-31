package com.ruoyi.telegram.command;


import com.ruoyi.telegram.KeyboardFactory;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;


/**
 * This command simply replies with a hello to the users command and
 * sends them the 'kind' words back, which they send via command parameters
 *
 * @author Timo Schulz (Mit0x2)
 */

@Component
public class ReportCommand extends BotCommand {


    public ReportCommand() {
        super("report", "数据统计");
    }

    @Override
    @SneakyThrows
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        String userName = chat.getUserName();
        if (userName == null || userName.isEmpty()) {
            userName = user.getFirstName() + " " + user.getLastName();
        }

        String messageTextBuilder = "您好 " + userName + "\n请选择时间:";

        SendMessage answer = new SendMessage();
        answer.setChatId(chat.getId().toString());
        answer.setText(messageTextBuilder);
        answer.setReplyMarkup(KeyboardFactory.get7days());
        absSender.execute(answer);

    }
}