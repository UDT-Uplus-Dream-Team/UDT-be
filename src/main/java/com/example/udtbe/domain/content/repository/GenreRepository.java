package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.Category;
import com.example.udtbe.domain.content.entity.Genre;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    Optional<Genre> findByGenreTypeAndCategory(GenreType genreType, Category category);
}
