package com.liuyang.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyang.admin.annotation.RequirePermission;
import com.liuyang.admin.common.Result;
import com.liuyang.admin.dto.UserCreateDTO;
import com.liuyang.admin.dto.UserRoleAssignDTO;
import com.liuyang.admin.dto.UserUpdateDTO;
import com.liuyang.admin.entity.Role;
import com.liuyang.admin.entity.User;
import com.liuyang.admin.service.RoleService;
import com.liuyang.admin.service.UserService;
import com.liuyang.admin.vo.PageVO;
import com.liuyang.admin.vo.RoleVO;
import com.liuyang.admin.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "用户管理") // 接口分组（加在 Controller 类上）
@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Operation(summary = "分页查询用户", description = "支持按 username 模糊搜索") //  接口说明（加在方法上）summary：接口标题 description：详细说明
    @GetMapping
    public Result<PageVO<UserVO>> page(
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "用户名（模糊搜索，可选）") @RequestParam(required = false) String username) {
        Page<User> userPage = userService.page(page, size, username);

        PageVO<UserVO> pageVO = new PageVO<>();
        pageVO.setRecords(userPage.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList()));
        pageVO.setTotal(userPage.getTotal());
        pageVO.setCurrent(userPage.getCurrent());
        pageVO.setSize(userPage.getSize());
        pageVO.setPages(userPage.getPages());

        return Result.success(pageVO);
    }

    @Operation(summary = "根据 ID 查询用户")
    @GetMapping("/{id}")
    public Result<UserVO> getById(
            @Parameter(description = "用户 ID") @PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }
        return Result.success(toVO(user));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public Result<UserVO> create(@Valid @RequestBody UserCreateDTO dto) {
        User user = userService.create(dto);
        return Result.success(toVO(user));
    }

    @Operation(summary = "修改用户")
    @PutMapping("/{id}")
    public Result<UserVO> update(
            @Parameter(description = "用户 ID") @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO dto) {
        User user = userService.update(id, dto);
        return Result.success(toVO(user));
    }

    @Operation(summary = "删除用户")
    @RequirePermission("user:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "用户 ID") @PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

    @Operation(summary = "查询用户已绑定的角色")
    @GetMapping("/{id}/roles")
    public Result<List<RoleVO>> listRoles(
            @Parameter(description = "用户 ID") @PathVariable Long id) {
        List<RoleVO> roles = roleService.listByUserId(id).stream()
                .map(this::toRoleVO)
                .collect(Collectors.toList());
        return Result.success(roles);
    }

    @Operation(summary = "给用户绑定角色", description = "覆盖式绑定：先清空该用户原有角色，再写入 roleIds")
    @RequirePermission("role:assign")
    @PostMapping("/{id}/roles")
    public Result<Void> assignRoles(
            @Parameter(description = "用户 ID") @PathVariable Long id,
            @Valid @RequestBody UserRoleAssignDTO dto) {
        roleService.assignRoles(id, dto);
        return Result.success();
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    private RoleVO toRoleVO(Role role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }
}
