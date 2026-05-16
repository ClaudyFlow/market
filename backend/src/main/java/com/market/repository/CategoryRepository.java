package com.market.repository;

import com.market.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByStatusOrderBySortOrderAsc(String status);

    List<Category> findByParentIdOrderBySortOrderAsc(Long parentId);

    List<Category> findByParentIdIsNullOrderBySortOrderAsc();

    Category findByName(String name);
}
