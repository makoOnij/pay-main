package com.ruoyi.telegram.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("sys_telegram")
public class Chat {
    @TableId(type = IdType.INPUT)
    private Long id;
    private String title;
    private Long tenantId;
    private String tenantName;
}
