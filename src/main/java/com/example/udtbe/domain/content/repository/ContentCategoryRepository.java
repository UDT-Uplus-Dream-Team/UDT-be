package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentCategoryRepository extends JpaRepository<ContentCategory, Long> {

    void deleteAllByContent(Content content);
}
