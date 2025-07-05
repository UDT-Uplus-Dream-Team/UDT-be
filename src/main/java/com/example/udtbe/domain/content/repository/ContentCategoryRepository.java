package com.example.udtbe.domain.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.udtbe.entity.ContentCategory;

public interface ContentCategoryRepository extends JpaRepository<ContentCategory, Long> {
}
