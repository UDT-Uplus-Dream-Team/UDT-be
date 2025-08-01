package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContentRepository extends JpaRepository<Content, Long>, ContentRepositoryCustom {

    List<Content> findAllById(Iterable<Long> contentIds);

    @Query("""
            select g.genreType
            from Content c
            join c.contentGenres cg
            join cg.genre g
            where c.id = :contentId
            """)
    List<GenreType> findGenreTypesByContentId(Long contentId);
}
