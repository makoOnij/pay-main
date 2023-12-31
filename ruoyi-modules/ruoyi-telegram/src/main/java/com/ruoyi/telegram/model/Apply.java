package com.ruoyi.telegram.model;

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
@TableName("apply")
public class Apply {
    private Long id;
    private Long userId;
    private String userName;
    private Long chatId;
    private String chatName;
    private Integer apState;
    private Date createTime;
}
