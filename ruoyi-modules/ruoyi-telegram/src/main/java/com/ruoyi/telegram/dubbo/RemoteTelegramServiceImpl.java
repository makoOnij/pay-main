package com.ruoyi.telegram.dubbo;

import com.ruoyi.common.core.dto.PayOrderDto;
import com.ruoyi.telegram.api.RemoteTelegramService;
import com.ruoyi.telegram.config.CacheUtils;
import com.ruoyi.telegram.model.Chat;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Optional;

/**
 * 用户服务
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteTelegramServiceImpl implements RemoteTelegramService {


    @Autowired
    private AbsSender sender;

    /**
     * 队列发送消息
     *
     * @param build
     */
    @Override
    @SneakyThrows
    public void send(PayOrderDto build) {
        SendMessage sendMessage = new SendMessage();
        Optional<Chat> first = CacheUtils.chats.stream().filter(p -> p.getId().equals(build.getTenantId())).findFirst();
        if (first.isPresent()) {
            sendMessage.setChatId(first.get().getId());
            sender.execute(sendMessage);
        }
    }
}
