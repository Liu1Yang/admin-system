package com.liuyang.admin.mq;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLogMessage {

    private Long userId;

    private String username;

    private String module;

    private String action;

    private String method;

    private String uri;

    private String ip;

    private Boolean success;

    private Integer durationMs;

    private LocalDateTime occurredAt;
}
