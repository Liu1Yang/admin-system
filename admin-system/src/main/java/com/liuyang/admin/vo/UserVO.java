package com.liuyang.admin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO { // 对应接口返回，直接返回Entity会暴露password

    private Long id;

    private String username;

    private String nickname;

    private LocalDateTime createTime;
}
