package com.liuyang.admin.common;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException { // 继承Java异常类（目的：让他变成异常类，可以throw)

    private final Integer code; // 自己加的字段  业务错误码，如 400、404

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
