package com.ruoyi.boss.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class FlowTotalDto implements Serializable {
    private BigDecimal amountTotal;
    private Integer countTotal;
    private BigDecimal feeTotal;

    public static FlowTotalDto buildEmpty() {
        FlowTotalDto dto = new FlowTotalDto();
        dto.setAmountTotal(BigDecimal.ZERO);
        dto.setCountTotal(0);
        dto.setFeeTotal(BigDecimal.ZERO);
        return dto;
    }
}
