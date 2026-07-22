package com.liuyang.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyang.admin.annotation.RequirePermission;
import com.liuyang.admin.common.Result;
import com.liuyang.admin.common.UserContext;
import com.liuyang.admin.dto.ProductCreateDTO;
import com.liuyang.admin.dto.ProductStatusUpdateDTO;
import com.liuyang.admin.dto.ProductUpdateDTO;
import com.liuyang.admin.entity.Product;
import com.liuyang.admin.service.CategoryService;
import com.liuyang.admin.service.ProductService;
import com.liuyang.admin.vo.PageVO;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Operation(summary = "分页查询商品", description = "支持名称模糊、分类、状态、价格区间筛选")
    @GetMapping
    public Result<PageVO<ProductVO>> page(
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "商品名称（模糊）") @RequestParam(required = false) String name,
            @Parameter(description = "分类 ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "状态：0下架 1上架") @RequestParam(required = false) Integer status,
            @Parameter(description = "最低价") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "最高价") @RequestParam(required = false) BigDecimal maxPrice) {
        Page<Product> productPage = productService.page(page, size, name, categoryId, status, minPrice, maxPrice); // 接收过滤条件并执行分页查询

        List<Product> records = productPage.getRecords();
        Map<Long, String> categoryNameMap = categoryService.mapNameByIds(  // 提取分类ID，批量查询分类名称（防 N+1 性能杀手，第 10-13 行）
                records.stream().map(Product::getCategoryId).collect(Collectors.toSet())
        );

        PageVO<ProductVO> pageVO = new PageVO<>();  // 转换 VO 并组装分页结果（第 15-22 行）
        pageVO.setRecords(records.stream()
                .map(product -> toVO(product, categoryNameMap))
                .collect(Collectors.toList()));
        pageVO.setTotal(productPage.getTotal());
        pageVO.setCurrent(productPage.getCurrent());
        pageVO.setSize(productPage.getSize());
        pageVO.setPages(productPage.getPages());
        return Result.success(pageVO);
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

    @Operation(summary = "商品上下架", description = "上架时库存必须大于 0")
    @RequirePermission("product:write")
    @PutMapping("/{id}/status")
    public Result<ProductVO> updateStatus(
            @Parameter(description = "商品 ID") @PathVariable Long id,
            @Valid @RequestBody ProductStatusUpdateDTO dto) {
        Product product = productService.updateStatus(id, dto.getStatus());
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
        Map<Long, String> categoryNameMap = categoryService.mapNameByIds(
                java.util.Collections.singleton(product.getCategoryId())
        );
        return toVO(product, categoryNameMap);
    }

    private ProductVO toVO(Product product, Map<Long, String> categoryNameMap) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo); // 拷贝商品的字段
        vo.setCategoryName(categoryNameMap.get(product.getCategoryId()));  // 从Map里取出分类名称塞进去
        return vo;
    }
}
