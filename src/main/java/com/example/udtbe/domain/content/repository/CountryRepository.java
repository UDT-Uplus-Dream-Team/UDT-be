package com.example.udtbe.domain.content.repository;

import com.example.udtbe.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {

}
