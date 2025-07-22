package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.ContentMetadata;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContentMetadataRepository extends JpaRepository<ContentMetadata, Long> {

    Optional<ContentMetadata> findByContent_Id(Long contentId);

    List<ContentMetadata> findByIsDeletedFalse();

    @Query("select cm.id from ContentMetadata cm where cm.content.id = :contentId")
    Optional<Long> findIdByContent_Id(Long contentId);
}
