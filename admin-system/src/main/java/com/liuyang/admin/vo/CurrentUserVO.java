package com.liuyang.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "当前登录用户信息（含角色与权限）")
public class CurrentUserVO {

    @Schema(description = "用户 ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "角色列表")
    private List<RoleVO> roles;

    @Schema(description = "权限编码列表，前端可据此控制按钮显示")
    private List<PermissionVO> permissions;
}
