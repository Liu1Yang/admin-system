package com.liuyang.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "权限信息")
public class PermissionVO {

    @Schema(description = "权限编码")
    private String code;

    @Schema(description = "权限名称")
    private String name;
}
