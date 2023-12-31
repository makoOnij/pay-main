package com.ruoyi.boss.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeriesDto implements Serializable {
    private String name;
    private String type="line";
    private String stack="Total";

    private Object[] data;
}
