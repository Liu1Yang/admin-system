package com.liuyang.admin.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class UserUpdateDTO {

    @Size(min = 6, max = 20, message = "密码长度为 6-20")
    private String password;

    private String nickname;
}
