package com.ruoyi.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态
 *
 * @author ruoyi
 */
@Getter
@AllArgsConstructor
public enum OpenStatus {
    OPEN(0, "开启"), CLOSE(1, "关闭");

    private final Integer code;
    private final String info;

}
