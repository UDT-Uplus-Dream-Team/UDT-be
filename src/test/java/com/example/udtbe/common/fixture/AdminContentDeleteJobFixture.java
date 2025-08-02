package com.example.udtbe.common.fixture;


import com.example.udtbe.domain.batch.entity.AdminContentDeleteJob;
import com.example.udtbe.domain.batch.entity.enums.BatchStepStatus;

public class AdminContentDeleteJobFixture {

    public static AdminContentDeleteJob createPendingJob(Long memberId, Long contentId) {

        return AdminContentDeleteJob.of(BatchStepStatus.PENDING, memberId, contentId);
    }
}