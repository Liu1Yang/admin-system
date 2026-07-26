package com.liuyang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema(description = "用户绑定角色请求")
public class UserRoleAssignDTO {

    @Schema(description = "角色 ID 列表，传空数组表示清空该用户所有角色", example = "[1]")
    @NotNull(message = "roleIds 不能为 null")
    private List<Long> roleIds;
}
