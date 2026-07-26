package com.liuyang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Schema(description = "新增分类请求")
public class CategoryCreateDTO {

    @Schema(description = "分类名称", example = "手机")
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称最长 50")
    private String name;

    @Schema(description = "父分类 ID，0 表示顶级", example = "1")
    @NotNull(message = "parentId 不能为 null")
    private Long parentId;

    @Schema(description = "排序值，越小越靠前", example = "1")
    private Integer sort;
}
