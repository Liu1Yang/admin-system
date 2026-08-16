package com.liuyang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "刷新 Token 请求")
public class RefreshTokenDTO {

    @NotBlank(message = "refreshToken 不能为空")
    @Schema(description = "Refresh Token")
    private String refreshToken;
}
