package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentGenreRepository extends JpaRepository<ContentGenre, Long> {

    void deleteAllByContent(Content content);
}
