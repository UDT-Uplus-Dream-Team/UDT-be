package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentDirector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentDirectorRepository extends JpaRepository<ContentDirector, Long> {

    void deleteAllByContent(Content content);
}
