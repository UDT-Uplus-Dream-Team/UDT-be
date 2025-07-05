package com.example.udtbe.domain.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.udtbe.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
