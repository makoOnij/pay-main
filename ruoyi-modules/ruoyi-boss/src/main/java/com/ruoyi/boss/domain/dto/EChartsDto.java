package com.ruoyi.boss.domain.dto;

import cn.hutool.core.date.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通道列表业务对象
 *
 * @author ruoyi
 * @date 2023-11-24
 */

@Data
public class EChartsDto implements Serializable {


    private String[] legend;


    private Object[] days;

    private List<SeriesDto> seriesDtos;

    public static EChartsDto buildEmpty(){
        EChartsDto dto=new EChartsDto();
        LocalDate beforeDay = LocalDate.now().minusDays(7);
        List<LocalDate> days= new ArrayList<>();
        while (true){
            if(beforeDay.isEqual(LocalDate.now()))
                break;
            days.add(beforeDay);
            beforeDay=beforeDay.plusDays(1);
        }
        dto.setDays(days.toArray());
        dto.setLegend(new String[]{"余额金额","充值金额","提现金额","代付金额","代付手续费"});
        List<SeriesDto> collect = Arrays.stream(new String[]{"余额金额", "充值金额", "提现金额", "代付金额", "代付手续费"}).map(
                p -> {
                    SeriesDto md = new SeriesDto();
                    md.setName(p);
                    md.setData(new Object[]{0, 0, 0, 0, 0, 0,0});
                    return md;
                }
        ).collect(Collectors.toList());
        dto.setSeriesDtos(collect);
        return dto;
    }
    
}
