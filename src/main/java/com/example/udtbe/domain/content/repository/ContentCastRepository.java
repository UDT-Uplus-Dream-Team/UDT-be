package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentCast;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentCastRepository extends JpaRepository<ContentCast, Long> {

    void deleteAllByContent(Content content);
}
