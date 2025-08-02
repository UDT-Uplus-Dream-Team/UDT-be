package com.example.udtbe.domain.batch.entity;


import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.batch.entity.enums.BatchStatus;
import com.example.udtbe.global.entity.TimeBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private BatchStatus status;

    private Long memberId;

    private Long contentId;


    @Builder(access = PRIVATE)
    private AdminContentDeleteJob(BatchStatus status, Long memberId, Long contentId) {
        this.status = status;
        this.memberId = memberId;
        this.contentId = contentId;
    }

    public static AdminContentDeleteJob of(BatchStatus status, Long memberId, Long contentId) {
        return AdminContentDeleteJob.builder()
                .status(status)
                .memberId(memberId)
                .contentId(contentId)
                .build();
    }

    public void changeStatus(BatchStatus status) {
        this.status = status;
    }
}

