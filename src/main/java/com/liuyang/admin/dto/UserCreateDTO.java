package com.liuyang.admin.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data // 包含 Getter 和 Setter
public class UserCreateDTO {

    @NotBlank(message = "用户名不能为空") // 参数校验：校验失败 → GlobalExceptionHandler 捕获 → 返回 Result.fail(400, message)。
    @Size(min = 3, max = 50, message = "用户名长度为 3-50")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为 6-20")
    private String password;

    private String nickname;
}
