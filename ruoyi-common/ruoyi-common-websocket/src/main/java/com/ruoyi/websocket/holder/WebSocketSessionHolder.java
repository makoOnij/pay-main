package com.ruoyi.websocket.holder;

import cn.hutool.core.map.MapUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocketSession 用于保存当前所有在线的会话信息
 *
 * @author zendwang
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebSocketSessionHolder {
    private static final Map<Long, Map<Long, WebSocketSession>> TENANT_SESSION_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, WebSocketSession> USER_SESSION_MAP = new ConcurrentHashMap<>();

    public static void addSession(Long sessionKey, WebSocketSession session) {
        USER_SESSION_MAP.put(sessionKey, session);
    }

    public static void addSessionTenant(Long tenantId, Long sessionKey, WebSocketSession session) {
        TENANT_SESSION_MAP.put(tenantId, MapUtil.of(sessionKey, session));
    }

    public static void removeSession(Long sessionKey) {
        if (USER_SESSION_MAP.containsKey(sessionKey)) {
            USER_SESSION_MAP.remove(sessionKey);
        }
    }

    public static void removeSessionTenant(Long tenantId, Long sessionKey) {
        if (TENANT_SESSION_MAP.containsKey(tenantId)) {
            Map<Long, WebSocketSession> longWebSocketSessionMap = TENANT_SESSION_MAP.get(tenantId);
            if (longWebSocketSessionMap.containsKey(sessionKey))
                longWebSocketSessionMap.remove(sessionKey);
        }
    }

    public static WebSocketSession getSessions(Long sessionKey) {
        return USER_SESSION_MAP.get(sessionKey);
    }

    public static Map<Long, WebSocketSession> getTenantSessions(Long sessionKey) {
        return TENANT_SESSION_MAP.get(sessionKey);
    }

    public static Set<Long> getSessionsAll() {
        return USER_SESSION_MAP.keySet();
    }

    public static Boolean existSession(Long sessionKey) {
        return USER_SESSION_MAP.containsKey(sessionKey);
    }
}
