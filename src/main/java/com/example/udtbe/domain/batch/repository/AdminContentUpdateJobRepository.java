package com.example.udtbe.domain.batch.repository;


import com.example.udtbe.domain.batch.entity.AdminContentUpdateJob;
import com.example.udtbe.domain.batch.entity.enums.BatchStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminContentUpdateJobRepository extends
        JpaRepository<AdminContentUpdateJob, Long> {

    List<AdminContentUpdateJob> findAllByStatus(BatchStatus status);
}
