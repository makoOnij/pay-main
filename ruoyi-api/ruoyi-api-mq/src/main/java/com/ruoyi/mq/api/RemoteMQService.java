package com.ruoyi.mq.api;


import com.ruoyi.mq.api.model.AbstractMQ;

/**
 * 订单服务
 *
 * @author Lion Li
 */
public interface RemoteMQService {

    /**
     * 队列发送消息
     * @param build
     */
    void send(AbstractMQ build);
}
