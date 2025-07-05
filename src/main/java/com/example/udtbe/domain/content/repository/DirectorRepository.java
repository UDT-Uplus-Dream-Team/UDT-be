package com.example.udtbe.domain.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.udtbe.entity.Director;

public interface DirectorRepository extends JpaRepository<Director, Long> {
}
