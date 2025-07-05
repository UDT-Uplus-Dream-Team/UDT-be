package com.example.udtbe.domain.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.udtbe.entity.ContentPlatform;

public interface ContentPlatformRepository extends JpaRepository<ContentPlatform, Long> {
}
