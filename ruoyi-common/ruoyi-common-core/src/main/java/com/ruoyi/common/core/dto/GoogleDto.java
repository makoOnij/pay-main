package com.ruoyi.common.core.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
public class GoogleDto {
    /**
     * google密钥
     */
    @NotBlank(message = "谷歌密钥不能为空")
    private String googleSecret;
    /**
     * 手机上显示的验证码
     */
    @NotNull(message = "验证码不能为空")
    private Long code;
    @NotNull(message = "用户ID不能为空")
    private Long userId;

}
