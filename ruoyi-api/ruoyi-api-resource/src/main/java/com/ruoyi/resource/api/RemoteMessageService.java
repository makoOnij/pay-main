package com.ruoyi.resource.api;

import com.ruoyi.resource.api.domain.WebsocketMessage;

/**
 * 消息服务
 *
 * @author Lion Li
 */
public interface RemoteMessageService {

    /**
     * 发送消息
     *
     * @param sessionKey session主键 一般为用户id
     * @param message    消息文本
     */
    void sendMessage(Long sessionKey, WebsocketMessage message);

    void publishAll(WebsocketMessage message);

    void sendMessageTenant(Long tenantId, WebsocketMessage message);
}
