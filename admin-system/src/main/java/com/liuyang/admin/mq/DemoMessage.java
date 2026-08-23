package com.liuyang.admin.mq;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class DemoMessage implements Serializable {  // 消息体

    private static final long serialVersionUID = 1L;

    private String content;  // 消息内容

    private String sender;  //  发送者

    private LocalDateTime sentAt; // 发送时间
}
