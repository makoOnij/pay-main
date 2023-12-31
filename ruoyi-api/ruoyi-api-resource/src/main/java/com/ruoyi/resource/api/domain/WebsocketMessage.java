package com.ruoyi.resource.api.domain;

import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

/**
 * 文件信息
 *
 * @author ruoyi
 */
@Data
public class WebsocketMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    public static WebsocketMessage build(MessageType messageType, String message) {
        WebsocketMessage websocketMessage = new WebsocketMessage();
        websocketMessage.setType(messageType);
        websocketMessage.setMessage(message);
        return websocketMessage;
    }

    @Getter
    public enum MessageType {

        ORDER(1, "新订单"),
        AUDIT(2, "审核成功"),
        WITHDRAW(3, "提现"),
        CHANNEL(4, "通道"),
        ;
        private int code;

        private String msg;

        MessageType(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

    }


    private MessageType type;

    private String message;


}
