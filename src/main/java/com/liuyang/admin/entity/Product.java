package com.liuyang.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long categoryId;

    private BigDecimal price;

    private Integer stock;

    /**
     * 0下架 1上架
     */
    private Integer status;

    private String coverUrl;

    private String description;

    private Long creatorId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
