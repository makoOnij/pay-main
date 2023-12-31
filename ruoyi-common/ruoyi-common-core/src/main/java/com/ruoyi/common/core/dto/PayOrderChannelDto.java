package com.ruoyi.common.core.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 代付渠道
 */
@Data
@TableName("pay_order_channel")
public class PayOrderChannelDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long orderId;
    private String orderNo;
    private Long wayId;
    private String errorCode;
    private String errorMsg;
    private Integer retryCount;
    /**
     * 状态0 待处理  1 回调成功 2回调失败
     */
    private Integer status;


}
