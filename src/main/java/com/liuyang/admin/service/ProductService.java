package com.liuyang.admin.service;

import com.liuyang.admin.dto.ProductCreateDTO;
import com.liuyang.admin.dto.ProductUpdateDTO;
import com.liuyang.admin.entity.Product;

public interface ProductService {

    Product getById(Long id);

    Product create(ProductCreateDTO dto, Long creatorId);

    Product update(Long id, ProductUpdateDTO dto);

    void delete(Long id);
}
