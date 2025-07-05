package com.example.udtbe.domain.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.udtbe.entity.Content;

public interface ContentRepository extends JpaRepository<Content, Long> {
}
