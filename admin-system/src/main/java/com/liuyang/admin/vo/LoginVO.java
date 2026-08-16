package com.liuyang.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录响应")
public class LoginVO {

    @Schema(description = "Access Token（短效，请求 API 时使用）")
    private String accessToken;

    @Schema(description = "Refresh Token（长效，仅用于刷新 Access Token）")
    private String refreshToken;

    @Schema(description = "用户信息（含角色与权限）")
    private CurrentUserVO user;
}
