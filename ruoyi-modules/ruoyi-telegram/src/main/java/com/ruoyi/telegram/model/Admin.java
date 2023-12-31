package com.ruoyi.telegram.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("admin")
public class Admin {
    @TableId(type = IdType.INPUT)
    private String userName;
    private Long userId;
    private Date createTime;
    private String sword;

    @TableField(exist = false)
    private Boolean isValid;
    @TableField(exist = false)
    private Integer state = 0;
}
