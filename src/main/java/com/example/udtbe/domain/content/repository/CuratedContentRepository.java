package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.dto.request.CuratedContentGetRequest;
import com.example.udtbe.domain.content.entity.CuratedContent;
import com.example.udtbe.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuratedContentRepository extends JpaRepository<CuratedContent, Long>,
        CuratedContentQueryDSL {

    List<CuratedContent> getCuratedContentByCursor(CuratedContentGetRequest request, Member member);

    Optional<CuratedContent> findCuratedContentById(Long id);
}
