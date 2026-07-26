package com.liuyang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Schema(description = "新增用户请求")
public class UserCreateDTO {

    @Schema(description = "用户名", example = "testuser01")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度为 3-50")
    private String username;

    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为 6-20")
    private String password;

    @Schema(description = "昵称", example = "测试用户")
    private String nickname;
}
