package com.ruoyi.telegram.api;


import com.ruoyi.common.core.dto.PayOrderDto;

/**
 * 纸飞机服务
 */
public interface RemoteTelegramService {

    /**
     * 队列发送消息
     *
     * @param build
     */
    void send(PayOrderDto build);
}
