package com.ruoyi.resource.dubbo;


import com.ruoyi.common.core.utils.JsonUtils;
import com.ruoyi.resource.api.RemoteMessageService;
import com.ruoyi.resource.api.domain.WebsocketMessage;
import com.ruoyi.websocket.utils.WebSocketUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
 * 消息服务
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteMessageServiceImpl implements RemoteMessageService {


    /**
     * 发送消息
     *
     * @param sessionKey session主键 一般为用户id
     * @param message    消息文本
     */
    @Override
    public void sendMessage(Long sessionKey, WebsocketMessage message) {
        WebSocketUtils.sendMessage(sessionKey, JsonUtils.toJsonString(message));
    }

    @Override
    public void publishAll(WebsocketMessage message) {

        WebSocketUtils.publishAll(JsonUtils.toJsonString(message));
    }

    @Override
    public void sendMessageTenant(Long tenantId, WebsocketMessage message) {
        WebSocketUtils.sendMessageTenant(tenantId, JsonUtils.toJsonString(message));
    }
}
