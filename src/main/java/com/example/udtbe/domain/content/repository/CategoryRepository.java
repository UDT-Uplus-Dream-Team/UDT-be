package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
