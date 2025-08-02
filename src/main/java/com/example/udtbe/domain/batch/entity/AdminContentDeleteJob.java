package com.example.udtbe.domain.batch.entity;


import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.batch.entity.enums.BatchStepStatus;
import com.example.udtbe.domain.batch.util.TimeUtil;
import com.example.udtbe.global.entity.TimeBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminContentDeleteJob extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_content_delete_job_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private BatchStepStatus status;

    private LocalDateTime updateAt;

    private LocalDateTime finishedAt;

    private Long memberId;

    private Long contentId;


    @Builder(access = PRIVATE)
    private AdminContentDeleteJob(BatchStepStatus status, LocalDateTime updateAt, Long memberId,
            Long contentId) {
        this.status = status;
        this.updateAt = updateAt;
        this.memberId = memberId;
        this.contentId = contentId;
    }

    public static AdminContentDeleteJob of(BatchStepStatus status, Long memberId, Long contentId) {
        return AdminContentDeleteJob.builder()
                .status(status)
                .updateAt(getUpdateAt())
                .memberId(memberId)
                .contentId(contentId)
                .build();
    }

    public void changeStatus(BatchStepStatus status) {
        this.status = status;
    }


    private static LocalDateTime getUpdateAt() {
        return TimeUtil.getUpdateTime();
    }

    public void finish() {
        finishedAt = LocalDateTime.now();
    }
}

