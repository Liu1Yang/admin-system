package com.liuyang.admin.mq;

import lombok.Data;

@Data
public class ProductCacheMessage {

    public static final String ACTION_REFRESH = "REFRESH";
    public static final String ACTION_DELETE = "DELETE";

    private Long productId;

    private String action;
}
