package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.Country;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByCountryName(String countryName);
}
