package com.ruoyi.mq.dubbo;

import com.ruoyi.mq.IMQSender;
import com.ruoyi.mq.api.RemoteMQService;
import com.ruoyi.mq.api.model.AbstractMQ;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteMqServiceImpl implements RemoteMQService {


    @Autowired
    private IMQSender sender;
    /**
     * 队列发送消息
     *
     * @param build
     */
    @Override
    public void send(AbstractMQ build) {
        sender.send(build);
    }
}
