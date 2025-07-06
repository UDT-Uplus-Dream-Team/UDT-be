package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.ContentMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentMetadataRepository extends JpaRepository<ContentMetadata, Long> {

}
