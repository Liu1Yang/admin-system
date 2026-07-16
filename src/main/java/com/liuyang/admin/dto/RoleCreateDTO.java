package com.liuyang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Schema(description = "新增角色请求")
public class RoleCreateDTO {

    @Schema(description = "角色编码", example = "EDITOR")
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码最长 50")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "角色编码须大写字母开头，仅含大写字母、数字、下划线") // 检查字符串是否匹配指定的正则规则。
    private String code;

    @Schema(description = "角色名称", example = "编辑")
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称最长 50")
    private String name;
}
