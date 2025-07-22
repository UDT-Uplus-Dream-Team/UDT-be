package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.CuratedContent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuratedContentRepository extends JpaRepository<CuratedContent, Long>,
        CuratedContentQueryDSL {

    Optional<CuratedContent> findCuratedContentById(Long id);

    Optional<CuratedContent> findByMemberIdAndContentId(Long memberId, Long contentId);

}
