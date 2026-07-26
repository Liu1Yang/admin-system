package com.liuyang.admin.service;

import com.liuyang.admin.dto.CategoryCreateDTO;
import com.liuyang.admin.dto.CategoryUpdateDTO;
import com.liuyang.admin.entity.Category;
import com.liuyang.admin.vo.CategoryTreeVO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CategoryService {

    List<CategoryTreeVO> listTree();

    Category getById(Long id);

    Map<Long, String> mapNameByIds(Collection<Long> ids);

    Category create(CategoryCreateDTO dto);

    Category update(Long id, CategoryUpdateDTO dto);

    void delete(Long id);
}
