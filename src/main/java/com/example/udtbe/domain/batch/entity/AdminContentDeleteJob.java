package com.example.udtbe.domain.batch.entity;


import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.batch.entity.enums.BatchStatus;
import com.example.udtbe.domain.batch.util.TimeUtil;
import com.example.udtbe.global.entity.TimeBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "admin_content_delete_job")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminContentDeleteJob extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_content_delete_job_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private BatchStatus status;

    private LocalDateTime scheduledAt;

    private LocalDateTime finishedAt;

    private Long memberId;

    private Long contentId;

    private String errorCode;

    private String errorMessage;

    private int retryCount = 0;

    private int skipCount = 0;

    private Long batchJobMetricId;

    @Builder(access = PRIVATE)
    private AdminContentDeleteJob(BatchStatus status, LocalDateTime scheduledAt, Long memberId,
            Long contentId) {
        this.status = status;
        this.scheduledAt = scheduledAt;
        this.memberId = memberId;
        this.contentId = contentId;
        this.retryCount = 0;
        this.skipCount = 0;
    }

    public static AdminContentDeleteJob of(BatchStatus status, Long memberId, Long contentId) {
        return AdminContentDeleteJob.builder()
                .status(status)
                .scheduledAt(getScheduledAt())
                .memberId(memberId)
                .contentId(contentId)
                .build();
    }

    public void changeStatus(BatchStatus status) {
        this.status = status;
    }

    public void setError(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public void incrementRetryCount() {
        this.retryCount += 1;
    }

    public void incrementSkipCount() {
        this.skipCount += 1;
    }

    private static LocalDateTime getScheduledAt() {
        return TimeUtil.getScheduledAt();
    }

    public void finish() {
        finishedAt = LocalDateTime.now();
    }

    public void setBatchJobMetricId(Long batchJobMetricId) {
        this.batchJobMetricId = batchJobMetricId;
    }
}

