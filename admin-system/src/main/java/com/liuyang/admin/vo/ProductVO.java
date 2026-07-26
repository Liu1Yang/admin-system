package com.liuyang.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "商品信息")
public class ProductVO {

    @Schema(description = "商品 ID")
    private Long id;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "分类 ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "售价")
    private BigDecimal price;

    @Schema(description = "库存")
    private Integer stock;

    @Schema(description = "状态：0下架 1上架")
    private Integer status;

    @Schema(description = "封面图 URL")
    private String coverUrl;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "创建人 ID")
    private Long creatorId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
