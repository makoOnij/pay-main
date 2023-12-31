package com.ruoyi.telegram;

import com.ruoyi.boss.api.dto.DataReportDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;


public class ResponseHandler {
    private static Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    public static SendMessage unexpectedMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("输入有误!");
        return sendMessage;
    }

    public static SendMessage sendReport(long chatId, List<DataReportDto> dataReportDtos) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("| 商户名 | 总资产|余额|冻结金额|充值金额|提现金额|代付金额|手续费|");
        dataReportDtos.forEach(item -> {
            stringBuilder.append("|").append(item.getTenantName()).append("|").append(item.getTotalAmount()).append("|").append(item.getTotalBalance()).append("|").append(item.getTotalFreeze()).append("|").append(item.getTotalCharge()).append("|").append(item.getTotalWithdraw()).append("|").append(item.getTotalPayfor()).append("|").append(item.getTotalFee()).append("|");
        });

        sendMessage.enableMarkdown(true);
        sendMessage.setText(stringBuilder.toString());
        return sendMessage;

    }
}

