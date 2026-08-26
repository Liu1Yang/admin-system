package com.liuyang.admin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLogVO {

    private Long id;

    private Long userId;

    private String username;

    private String module;

    private String action;

    private String method;

    private String uri;

    private String ip;

    private Boolean success;

    private Integer durationMs;

    private LocalDateTime createTime;
}
