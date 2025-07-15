package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.Cast;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CastRepository extends JpaRepository<Cast, Long> {

    Optional<Cast> findByCastNameAndCastImageUrl(String castName, String castImageUrl);
}
