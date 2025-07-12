package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.Director;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectorRepository extends JpaRepository<Director, Long> {

    Optional<Director> findByDirectorName(String directorName);
}
