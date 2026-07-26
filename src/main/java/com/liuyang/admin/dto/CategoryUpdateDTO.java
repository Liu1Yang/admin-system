package com.liuyang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Schema(description = "修改分类请求")
public class CategoryUpdateDTO {

    @Schema(description = "分类名称", example = "智能手机")
    @Size(max = 50, message = "分类名称最长 50")
    private String name;

    @Schema(description = "父分类 ID，0 表示顶级")
    private Long parentId;

    @Schema(description = "排序值")
    private Integer sort;
}
