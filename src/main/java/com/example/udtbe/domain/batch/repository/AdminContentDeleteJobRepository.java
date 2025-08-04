package com.example.udtbe.domain.batch.repository;

import com.example.udtbe.domain.batch.entity.AdminContentDeleteJob;
import com.example.udtbe.domain.batch.entity.enums.BatchStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminContentDeleteJobRepository extends
        JpaRepository<AdminContentDeleteJob, Long> {


    List<AdminContentDeleteJob> findByStatus(BatchStatus status);
}
