package com.liuyang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Schema(description = "修改用户请求")
public class UserUpdateDTO {

    @Schema(description = "新密码（可选）", example = "123456")
    @Size(min = 6, max = 20, message = "密码长度为 6-20")
    private String password;

    @Schema(description = "昵称（可选）", example = "新昵称")
    private String nickname;
}
