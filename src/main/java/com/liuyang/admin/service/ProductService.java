package com.liuyang.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyang.admin.dto.ProductCreateDTO;
import com.liuyang.admin.dto.ProductUpdateDTO;
import com.liuyang.admin.entity.Product;

import java.math.BigDecimal;

public interface ProductService {

    Page<Product> page(int pageNum, int pageSize, String name, Long categoryId,
                       Integer status, BigDecimal minPrice, BigDecimal maxPrice);

    Product getById(Long id);

    Product create(ProductCreateDTO dto, Long creatorId);

    Product update(Long id, ProductUpdateDTO dto);

    void delete(Long id);
}
