package com.ruoyi.boss.domain.dto;

import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 代付通道业务对象
 *
 * @author ruoyi
 * @date 2023-11-24
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class PayChannelBatchDto extends BaseEntity {

    /**
     *
     */

    private List<Long> ids;


    /**
     * 状态
     */
    private Integer status;

}
