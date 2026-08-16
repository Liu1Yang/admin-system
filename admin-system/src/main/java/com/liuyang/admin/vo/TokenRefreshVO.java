package com.liuyang.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "刷新 Token 响应")
public class TokenRefreshVO {

    @Schema(description = "新的 Access Token")
    private String accessToken;

    @Schema(description = "新的 Refresh Token（轮换）")
    private String refreshToken;
}
