package com.ruoyi.common.core.enums;

import com.ruoyi.common.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备类型
 * 针对多套 用户体系
 *
 * @author Lion Li
 */
@Getter
@AllArgsConstructor
public enum Currency {
    CNY("CNY"),


    USDT("USDT");

    private final String currencyType;

    public static Currency getCurrencyType(String str) {
        for (Currency value : values()) {
            if (StringUtils.contains(str, value.getCurrencyType())) {
                return value;
            }
        }
        throw new RuntimeException("'CurrencyType' not found By " + str);
    }
}
