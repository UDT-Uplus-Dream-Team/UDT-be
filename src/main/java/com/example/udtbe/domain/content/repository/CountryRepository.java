package com.example.udtbe.domain.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.udtbe.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
