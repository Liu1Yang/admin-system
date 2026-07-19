package com.liuyang.admin.controller;

import com.liuyang.admin.annotation.RequirePermission;
import com.liuyang.admin.common.Result;
import com.liuyang.admin.common.UserContext;
import com.liuyang.admin.dto.ProductCreateDTO;
import com.liuyang.admin.dto.ProductUpdateDTO;
import com.liuyang.admin.entity.Category;
import com.liuyang.admin.entity.Product;
import com.liuyang.admin.service.CategoryService;
import com.liuyang.admin.service.ProductService;
import com.liuyang.admin.vo.ProductVO;
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

@Tag(name = "商品管理")
@Validated
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @Operation(summary = "根据 ID 查询商品")
    @GetMapping("/{id}")
    public Result<ProductVO> getById(
            @Parameter(description = "商品 ID") @PathVariable Long id) {
        Product product = productService.getById(id);
        return Result.success(toVO(product));
    }

    @Operation(summary = "新增商品", description = "新建商品默认下架（status=0）")
    @RequirePermission("product:write")
    @PostMapping
    public Result<ProductVO> create(@Valid @RequestBody ProductCreateDTO dto) {
        Product product = productService.create(dto, UserContext.getUserId());
        return Result.success(toVO(product));
    }

    @Operation(summary = "修改商品")
    @RequirePermission("product:write")
    @PutMapping("/{id}")
    public Result<ProductVO> update(
            @Parameter(description = "商品 ID") @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO dto) {
        Product product = productService.update(id, dto);
        return Result.success(toVO(product));
    }

    @Operation(summary = "删除商品")
    @RequirePermission("product:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "商品 ID") @PathVariable Long id) {
        productService.delete(id);
        return Result.success();
    }

    private ProductVO toVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);
        Category category = categoryService.getById(product.getCategoryId());
        vo.setCategoryName(category.getName());
        return vo;
    }
}
