package com.example.udtbe.domain.batch.repository;

import com.example.udtbe.domain.batch.entity.BatchJobMetric;
import com.example.udtbe.domain.batch.entity.enums.BatchJobType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BatchJobMetricRepository extends
        JpaRepository<BatchJobMetric, Long> {

    @Query("SELECT j FROM BatchJobMetric j ORDER BY j.id ASC")
    List<BatchJobMetric> findAllByOrderByIdAsc();

    Optional<BatchJobMetric> findAdminContentJobMetricByType(BatchJobType type);
}
