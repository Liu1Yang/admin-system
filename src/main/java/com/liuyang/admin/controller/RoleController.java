package com.liuyang.admin.controller;

import com.liuyang.admin.common.Result;
import com.liuyang.admin.dto.RoleCreateDTO;
import com.liuyang.admin.entity.Role;
import com.liuyang.admin.service.RoleService;
import com.liuyang.admin.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "角色管理")
@Validated
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "查询全部角色")
    @GetMapping
    public Result<List<RoleVO>> list() {
        List<RoleVO> list = roleService.listAll().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Operation(summary = "根据 ID 查询角色")
    @GetMapping("/{id}")
    public Result<RoleVO> getById(
            @Parameter(description = "角色 ID") @PathVariable Long id) {
        return Result.success(toVO(roleService.getById(id)));
    }

    @Operation(summary = "新增角色")
    @PostMapping
    public Result<RoleVO> create(@Valid @RequestBody RoleCreateDTO dto) {
        return Result.success(toVO(roleService.create(dto)));
    }

    private RoleVO toVO(Role role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }
}
