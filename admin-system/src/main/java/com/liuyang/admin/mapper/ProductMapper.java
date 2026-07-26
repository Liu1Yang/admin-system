package com.liuyang.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuyang.admin.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Select("SELECT COUNT(*) FROM product WHERE category_id = #{categoryId}")
    Long countByCategoryId(@Param("categoryId") Long categoryId);
}
