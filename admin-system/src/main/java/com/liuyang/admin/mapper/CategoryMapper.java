package com.liuyang.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuyang.admin.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    @Select("SELECT COUNT(*) FROM category WHERE parent_id = #{parentId}")
    Long countByParentId(@Param("parentId") Long parentId);
}
