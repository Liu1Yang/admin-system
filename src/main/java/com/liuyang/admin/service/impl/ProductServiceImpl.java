package com.liuyang.admin.service.impl;

import com.liuyang.admin.common.BusinessException;
import com.liuyang.admin.dto.ProductCreateDTO;
import com.liuyang.admin.dto.ProductUpdateDTO;
import com.liuyang.admin.entity.Category;
import com.liuyang.admin.entity.Product;
import com.liuyang.admin.mapper.ProductMapper;
import com.liuyang.admin.service.CategoryService;
import com.liuyang.admin.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProductServiceImpl implements ProductService {

    private static final int STATUS_OFF = 0; // 初始下架状态

    private final ProductMapper productMapper;
    private final CategoryService categoryService;

    public ProductServiceImpl(ProductMapper productMapper, CategoryService categoryService) {
        this.productMapper = productMapper;
        this.categoryService = categoryService;
    }

    @Override
    public Product getById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        return product;
    }

    @Override
    public Product create(ProductCreateDTO dto, Long creatorId) {
        validateCategory(dto.getCategoryId());

        Product product = new Product();
        product.setName(dto.getName());
        product.setCategoryId(dto.getCategoryId());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock() == null ? 0 : dto.getStock());
        product.setStatus(STATUS_OFF);
        product.setCoverUrl(dto.getCoverUrl());
        product.setDescription(dto.getDescription());
        product.setCreatorId(creatorId);
        productMapper.insert(product);
        return product;
    }

    @Override
    public Product update(Long id, ProductUpdateDTO dto) {
        Product product = getById(id);

        if (StringUtils.hasText(dto.getName())) {
            product.setName(dto.getName());
        }
        if (dto.getCategoryId() != null) {
            validateCategory(dto.getCategoryId());
            product.setCategoryId(dto.getCategoryId());
        }
        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }
        if (dto.getStock() != null) {
            product.setStock(dto.getStock());
        }
        if (dto.getCoverUrl() != null) {
            product.setCoverUrl(dto.getCoverUrl());
        }
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }

        productMapper.updateById(product);
        return product;
    }

    @Override
    public void delete(Long id) {
        getById(id);
        productMapper.deleteById(id);
    }

    private void validateCategory(Long categoryId) {
        categoryService.getById(categoryId);
    }
}
