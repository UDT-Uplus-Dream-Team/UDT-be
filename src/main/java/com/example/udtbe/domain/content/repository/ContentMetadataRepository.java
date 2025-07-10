package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.ContentMetadata;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentMetadataRepository extends JpaRepository<ContentMetadata, Long> {

    Optional<ContentMetadata> findByContent_Id(Long contentId);
}
