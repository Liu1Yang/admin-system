package com.liuyang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liuyang.admin.common.BusinessException;
import com.liuyang.admin.dto.CategoryCreateDTO;
import com.liuyang.admin.dto.CategoryUpdateDTO;
import com.liuyang.admin.entity.Category;
import com.liuyang.admin.mapper.CategoryMapper;
import com.liuyang.admin.mapper.ProductMapper;
import com.liuyang.admin.service.CategoryService;
import com.liuyang.admin.vo.CategoryTreeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final long ROOT_PARENT_ID = 0L;

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper, ProductMapper productMapper) {
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
    }

    @Override
    public List<CategoryTreeVO> listTree() {
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .orderByAsc(Category::getSort)
                        .orderByAsc(Category::getId)
        );
        return buildTree(categories);
    }

    @Override
    public Category getById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        return category;
    }

    @Override
    public Category create(CategoryCreateDTO dto) {
        validateParentId(dto.getParentId(), null);

        Category category = new Category();
        category.setName(dto.getName());
        category.setParentId(dto.getParentId());
        category.setSort(dto.getSort() == null ? 0 : dto.getSort());
        categoryMapper.insert(category);
        return category;
    }

    @Override
    public Category update(Long id, CategoryUpdateDTO dto) {
        Category category = getById(id);

        if (StringUtils.hasText(dto.getName())) {
            category.setName(dto.getName());
        }
        if (dto.getParentId() != null) {
            if (dto.getParentId().equals(id)) {
                throw new BusinessException(400, "父分类不能是自己");
            }
            validateParentId(dto.getParentId(), id);
            category.setParentId(dto.getParentId());
        }
        if (dto.getSort() != null) {
            category.setSort(dto.getSort());
        }

        categoryMapper.updateById(category);
        return category;
    }

    @Override
    public void delete(Long id) {
        getById(id);

        Long childCount = categoryMapper.countByParentId(id);
        if (childCount != null && childCount > 0) {
            throw new BusinessException(400, "存在子分类，无法删除");
        }

        Long productCount = productMapper.countByCategoryId(id);
        if (productCount != null && productCount > 0) {
            throw new BusinessException(400, "分类下存在商品，无法删除");
        }

        categoryMapper.deleteById(id);
    }

    private void validateParentId(Long parentId, Long currentId) {
        if (parentId == null || parentId == ROOT_PARENT_ID) {
            return;
        }
        Category parent = categoryMapper.selectById(parentId);
        if (parent == null) {
            throw new BusinessException(400, "父分类不存在");
        }
        if (currentId != null && isDescendant(currentId, parentId)) {
            throw new BusinessException(400, "不能将分类移动到其子分类下");
        }
    }

    private boolean isDescendant(Long ancestorId, Long nodeId) {
        Category node = categoryMapper.selectById(nodeId);
        while (node != null && node.getParentId() != null && node.getParentId() != ROOT_PARENT_ID) {
            if (node.getParentId().equals(ancestorId)) {
                return true;
            }
            node = categoryMapper.selectById(node.getParentId());
        }
        return false;
    }

    private List<CategoryTreeVO> buildTree(List<Category> categories) {
        Map<Long, CategoryTreeVO> nodeMap = new HashMap<>();
        for (Category category : categories) {
            nodeMap.put(category.getId(), toTreeVO(category));
        }

        List<CategoryTreeVO> roots = new ArrayList<>();
        for (Category category : categories) {
            CategoryTreeVO node = nodeMap.get(category.getId());
            if (category.getParentId() == null || category.getParentId() == ROOT_PARENT_ID) {
                roots.add(node);
                continue;
            }
            CategoryTreeVO parent = nodeMap.get(category.getParentId());
            if (parent == null) {
                roots.add(node);
                continue;
            }
            if (parent.getChildren() == null) {
                parent.setChildren(new ArrayList<>());
            }
            parent.getChildren().add(node);
        }

        sortTree(roots);
        return roots;
    }

    private void sortTree(List<CategoryTreeVO> nodes) {
        nodes.sort(Comparator.comparing(CategoryTreeVO::getSort).thenComparing(CategoryTreeVO::getId));
        for (CategoryTreeVO node : nodes) {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                sortTree(node.getChildren());
            }
        }
    }

    private CategoryTreeVO toTreeVO(Category category) {
        CategoryTreeVO vo = new CategoryTreeVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }
}
