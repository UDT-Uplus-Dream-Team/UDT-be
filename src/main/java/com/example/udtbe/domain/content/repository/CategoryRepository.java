package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.Category;
import com.example.udtbe.domain.content.entity.enums.CategoryType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCategoryType(CategoryType categoryType);

    boolean existsCategoryByCategoryType(CategoryType categoryType);
}
