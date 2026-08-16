package com.liuyang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登出请求（可选传 refreshToken 一并吊销）")
public class LogoutDTO {

    @Schema(description = "Refresh Token，可选")
    private String refreshToken;
}
