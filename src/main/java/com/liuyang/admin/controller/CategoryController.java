package com.liuyang.admin.controller;

import com.liuyang.admin.annotation.RequirePermission;
import com.liuyang.admin.common.Result;
import com.liuyang.admin.dto.CategoryCreateDTO;
import com.liuyang.admin.dto.CategoryUpdateDTO;
import com.liuyang.admin.entity.Category;
import com.liuyang.admin.service.CategoryService;
import com.liuyang.admin.vo.CategoryTreeVO;
import com.liuyang.admin.vo.CategoryVO;
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
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "商品分类")
@Validated
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "查询分类树")
    @GetMapping("/tree")
    public Result<List<CategoryTreeVO>> tree() {
        return Result.success(categoryService.listTree());
    }

    @Operation(summary = "根据 ID 查询分类")
    @GetMapping("/{id}")
    public Result<CategoryVO> getById(
            @Parameter(description = "分类 ID") @PathVariable Long id) {
        return Result.success(toVO(categoryService.getById(id)));
    }

    @Operation(summary = "新增分类")
    @RequirePermission("product:write")
    @PostMapping
    public Result<CategoryVO> create(@Valid @RequestBody CategoryCreateDTO dto) {
        return Result.success(toVO(categoryService.create(dto)));
    }

    @Operation(summary = "修改分类")
    @RequirePermission("product:write")
    @PutMapping("/{id}")
    public Result<CategoryVO> update(
            @Parameter(description = "分类 ID") @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateDTO dto) {
        return Result.success(toVO(categoryService.update(id, dto)));
    }

    @Operation(summary = "删除分类", description = "存在子分类时拒绝删除")
    @RequirePermission("product:write")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "分类 ID") @PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }

    private CategoryVO toVO(Category category) {
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }
}
