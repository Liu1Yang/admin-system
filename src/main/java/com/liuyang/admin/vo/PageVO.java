package com.liuyang.admin.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageVO<T> { // 统一分页返回格式

    private List<T> records; // 用户列表

    private Long total;

    private Long current; // 当前页码

    private Long size;

    private Long pages;
}
