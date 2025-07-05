package com.example.udtbe.domain.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.udtbe.entity.ContentCountry;

public interface ContentCountryRepository extends JpaRepository<ContentCountry, Long> {
}
