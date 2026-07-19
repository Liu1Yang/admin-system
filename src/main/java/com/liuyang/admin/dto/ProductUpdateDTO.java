package com.liuyang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@Schema(description = "修改商品请求")
public class ProductUpdateDTO {

    @Schema(description = "商品名称")
    @Size(max = 100, message = "商品名称最长 100")
    private String name;

    @Schema(description = "分类 ID")
    private Long categoryId;

    @Schema(description = "售价")
    @DecimalMin(value = "0.01", message = "售价必须大于 0")
    private BigDecimal price;

    @Schema(description = "库存")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    @Schema(description = "封面图 URL")
    @Size(max = 255, message = "封面图 URL 最长 255")
    private String coverUrl;

    @Schema(description = "商品描述")
    private String description;
}
