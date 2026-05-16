package com.market.controller;

import com.market.common.Result;
import com.market.entity.Category;
import com.market.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/category")
@CrossOrigin(origins = "*")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有分类（树形结构）
     */
    @GetMapping("/tree")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<Category>> getCategoryTree() {
        List<Category> roots = categoryService.getRootCategories();
        return Result.success(roots);
    }

    /**
     * 获取所有启用的分类
     */
    @GetMapping
    public Result<List<Category>> getAllCategories() {
        return Result.success(categoryService.getAllActiveCategories());
    }

    /**
     * 获取子分类
     */
    @GetMapping("/{parentId}/children")
    public Result<List<Category>> getChildren(@PathVariable Long parentId) {
        return Result.success(categoryService.getSubCategories(parentId));
    }

    /**
     * 创建分类
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Category> create(@RequestBody Category category) {
        Category created = categoryService.create(category);
        return Result.success(created);
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Category> update(@PathVariable Long id, @RequestBody Category updates) {
        Category updated = categoryService.update(id, updates);
        return updated != null ? Result.success(updated) : Result.error(404, "分类不存在");
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success(null);
    }

    /**
     * 批量更新排序
     */
    @PutMapping("/sort")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateSortOrder(@RequestBody List<Category> categories) {
        categoryService.batchUpdateSortOrder(categories);
        return Result.success(null);
    }
}
