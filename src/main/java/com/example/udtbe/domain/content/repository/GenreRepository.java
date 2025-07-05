package com.example.udtbe.domain.content.repository;

import com.example.udtbe.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {

}
