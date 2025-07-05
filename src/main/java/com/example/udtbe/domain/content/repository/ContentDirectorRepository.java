package com.example.udtbe.domain.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.udtbe.entity.ContentDirector;

public interface ContentDirectorRepository extends JpaRepository<ContentDirector, Long> {
}
