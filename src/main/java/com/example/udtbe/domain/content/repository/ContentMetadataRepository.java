package com.example.udtbe.domain.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.udtbe.entity.ContentMetadata;

public interface ContentMetadataRepository extends JpaRepository<ContentMetadata, Long> {
}
