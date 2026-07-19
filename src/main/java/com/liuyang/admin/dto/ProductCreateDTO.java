package com.liuyang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@Schema(description = "新增商品请求")
public class ProductCreateDTO {

    @Schema(description = "商品名称", example = "iPhone 15")
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称最长 100")
    private String name;

    @Schema(description = "分类 ID", example = "2")
    @NotNull(message = "分类 ID 不能为空")
    private Long categoryId;

    @Schema(description = "售价", example = "5999.00")
    @NotNull(message = "售价不能为空")
    @DecimalMin(value = "0.01", message = "售价必须大于 0")
    private BigDecimal price;

    @Schema(description = "库存", example = "100")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    @Schema(description = "封面图 URL")
    @Size(max = 255, message = "封面图 URL 最长 255")
    private String coverUrl;

    @Schema(description = "商品描述")
    private String description;
}
