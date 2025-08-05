package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.Platform;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformRepository extends JpaRepository<Platform, Long> {

    Optional<Platform> findByPlatformType(PlatformType platformType);

    boolean existsPlatformByPlatformType(PlatformType platformType);
}
