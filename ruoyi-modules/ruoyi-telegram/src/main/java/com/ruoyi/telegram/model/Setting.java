package com.ruoyi.telegram.model;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("setting")
public class Setting {
    private Long id;
    private String fromName;
    private String fromId;
    private String toName;
    private String toId;
    private Integer runState;
    private String sword;
    @TableField(exist = false)
    private Boolean isValid;

    @TableField(exist = false)
    private Boolean hasAdmin = true;

    public Boolean getIsValid() {
        if (ObjectUtil.isNotEmpty(fromName) && ObjectUtil.isNotEmpty(toName)) {
            return true;
        }
        return false;
    }
}
