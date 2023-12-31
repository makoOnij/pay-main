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
public enum AutoPayEnum {
    AUTO(0, "自动"), MANUAL(1, "手动审核");

    private final Integer code;
    private final String info;

}
