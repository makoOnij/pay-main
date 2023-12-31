package com.ruoyi.telegram.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.boss.api.RemoteBossService;
import com.ruoyi.boss.api.dto.DataReportDto;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.system.api.RemoteTenantService;
import com.ruoyi.telegram.ResponseHandler;
import com.ruoyi.telegram.config.BotConfig;
import com.ruoyi.telegram.config.CacheUtils;
import com.ruoyi.telegram.mapper.ChatMapper;
import com.ruoyi.telegram.model.Chat;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MessageProcessService {


    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private BotConfig botConfig;

    @DubboReference
    private RemoteBossService remoteBossService;

    @DubboReference
    private RemoteTenantService remoteTenantService;

    @SneakyThrows
    public void process(Update update, AbsSender sender) {

        //进群出群通知
        if (ObjectUtil.isNotEmpty(update.getMyChatMember())) {
            ChatMemberUpdated myChatMember = update.getMyChatMember();
            if (myChatMember.getNewChatMember().getStatus().equals("left")) {
                chatMapper.deleteById(myChatMember.getChat().getId());
                CacheUtils.chats.removeIf(p -> p.getId().equals(myChatMember.getChat().getId()));
                return;
            }

            if (ObjectUtil.isNotEmpty(myChatMember.getChat())
                    && myChatMember.getChat().getType().contains("group")) {
                Chat chat = Chat.builder().id(myChatMember.getChat().getId()).title(myChatMember.getChat().getTitle()).build();
                TenantDto tenant = remoteTenantService.getTenantByChatId(myChatMember.getChat().getId());
                if (ObjectUtil.isNotEmpty(tenant)) {
                    chat.setTenantId(tenant.getId());
                    chat.setTenantName(tenant.getTenantName());
                }
                CacheUtils.chats.add(chat);
                chatMapper.insertOrUpdate(chat);
                return;
            }

        }
        //check if the update has a message
        Message message = update.getMessage();

        if (message.hasText()) {
            String input = message.getText();
            if (input.startsWith(">>")) {
                Long chatId = message.getChatId();
                Optional<Chat> first = CacheUtils.chats.stream().filter(p -> p.getId().equals(chatId)).findFirst();
                first.ifPresent(m -> {

                });
                if (first.isPresent()) {
                    //查询报表
                    List<DataReportDto> reportByDay = remoteBossService.getReportByDay(first.get().getTenantId(), input);
                    if (ObjectUtil.isNotEmpty(reportByDay)) {
                        ResponseHandler.sendReport(chatId, reportByDay);
                        return;
                    }
                }
                sender.execute(ResponseHandler.unexpectedMessage(chatId));

            }
        }

    }

    public void initData() {
        //加载所有群组
        List<Chat> chats1 = chatMapper.selectList(Wrappers.emptyWrapper());
        CacheUtils.chats.addAll(chats1);
    }
}
