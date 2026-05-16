package com.market.service;

import com.market.entity.Category;
import com.market.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllActiveCategories() {
        return categoryRepository.findByStatusOrderBySortOrderAsc("ACTIVE");
    }

    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIdIsNullOrderBySortOrderAsc();
    }

    public List<Category> getSubCategories(Long parentId) {
        return categoryRepository.findByParentIdOrderBySortOrderAsc(parentId);
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Transactional
    public Category create(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Long id, Category updates) {
        Category existing = categoryRepository.findById(id).orElse(null);
        if (existing == null) return null;

        if (updates.getName() != null) existing.setName(updates.getName());
        if (updates.getIcon() != null) existing.setIcon(updates.getIcon());
        if (updates.getPath() != null) existing.setPath(updates.getPath());
        if (updates.getParentId() != null) existing.setParentId(updates.getParentId());
        if (updates.getSortOrder() != null) existing.setSortOrder(updates.getSortOrder());
        if (updates.getStatus() != null) existing.setStatus(updates.getStatus());

        return categoryRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    @Transactional
    public void batchUpdateSortOrder(List<Category> categories) {
        categoryRepository.saveAll(categories);
    }
}
