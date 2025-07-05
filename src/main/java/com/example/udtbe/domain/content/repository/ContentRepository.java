package com.example.udtbe.domain.content.repository;

import com.example.udtbe.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {

}
