package com.example.udtbe.domain.batch.repository;


import com.example.udtbe.domain.batch.entity.AdminContentRegisterJob;
import com.example.udtbe.domain.batch.entity.enums.BatchStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminContentRegisterJobRepository extends
        JpaRepository<AdminContentRegisterJob, Long> {

    List<AdminContentRegisterJob> findAllByStatus(BatchStatus status);
}
