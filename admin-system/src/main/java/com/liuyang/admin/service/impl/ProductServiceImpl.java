package com.liuyang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyang.admin.common.BusinessException;
import com.liuyang.admin.dto.ProductCreateDTO;
import com.liuyang.admin.dto.ProductUpdateDTO;
import com.liuyang.admin.entity.Product;
import com.liuyang.admin.mapper.ProductMapper;
import com.liuyang.admin.service.CategoryService;
import com.liuyang.admin.service.ProductService;
import com.liuyang.admin.service.cache.ProductCacheService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Service
public class ProductServiceImpl implements ProductService {

    private static final int STATUS_OFF = 0;
    private static final int STATUS_ON = 1;
    private static final String COVER_URL_PREFIX = "/uploads/";

    private final ProductMapper productMapper;
    private final CategoryService categoryService;
    private final ProductCacheService productCacheService;

    public ProductServiceImpl(ProductMapper productMapper,
                              CategoryService categoryService,
                              ProductCacheService productCacheService) {
        this.productMapper = productMapper;
        this.categoryService = categoryService;
        this.productCacheService = productCacheService;
    }

    @Override
    public Page<Product> page(int pageNum, int pageSize, String name, Long categoryId,
                              Integer status, BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new BusinessException(400, "最低价不能大于最高价");
        }

        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Product::getName, name);
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        if (minPrice != null) {
            wrapper.ge(Product::getPrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(Product::getPrice, maxPrice);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        return productMapper.selectPage(page, wrapper);
    }

    @Override
    public Product getById(Long id) {
        Product cached = productCacheService.getById(id);
        if (cached != null) {
            return cached;
        }

        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        productCacheService.set(product);
        return product;
    }

    @Override
    public Product create(ProductCreateDTO dto, Long creatorId) {
        validateCategory(dto.getCategoryId());
        validateCoverUrl(dto.getCoverUrl());

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
        productCacheService.set(product);
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
            if (product.getStatus() == STATUS_ON && dto.getStock() == 0) {
                throw new BusinessException(400, "上架商品库存不能为 0");
            }
            product.setStock(dto.getStock());
        }
        if (dto.getCoverUrl() != null) {
            validateCoverUrl(dto.getCoverUrl());
            product.setCoverUrl(dto.getCoverUrl());
        }
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }

        productMapper.updateById(product);
        productCacheService.delete(id);
        return product;
    }

    @Override
    public Product updateStatus(Long id, Integer status) {
        Product product = getById(id);
        validateStatus(status);

        if (status == STATUS_ON && (product.getStock() == null || product.getStock() <= 0)) {
            throw new BusinessException(400, "库存不足，无法上架");
        }

        product.setStatus(status);
        productMapper.updateById(product);
        productCacheService.delete(id);
        return product;
    }

    @Override
    public Product updateCover(Long id, String coverUrl) {
        validateCoverUrl(coverUrl);
        Product product = getById(id);
        product.setCoverUrl(coverUrl);
        productMapper.updateById(product);
        productCacheService.delete(id);
        return product;
    }

    @Override
    public void delete(Long id) {
        getById(id);
        productMapper.deleteById(id);
        productCacheService.delete(id);
    }

    private void validateCategory(Long categoryId) {
        categoryService.getById(categoryId);
    }

    private void validateStatus(Integer status) {
        if (status == null || (status != STATUS_OFF && status != STATUS_ON)) {
            throw new BusinessException(400, "status 只能为 0 或 1");
        }
    }

    private void validateCoverUrl(String coverUrl) {
        if (!StringUtils.hasText(coverUrl)) {
            return;
        }
        if (!coverUrl.startsWith(COVER_URL_PREFIX)) {
            throw new BusinessException(400, "封面图须为本系统上传的文件（/uploads/ 开头）");
        }
    }
}
