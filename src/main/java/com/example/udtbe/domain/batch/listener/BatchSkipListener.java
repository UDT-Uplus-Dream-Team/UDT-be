package com.example.udtbe.domain.batch.listener;

import com.example.udtbe.domain.batch.entity.AdminContentDeleteJob;
import com.example.udtbe.domain.batch.entity.AdminContentRegisterJob;
import com.example.udtbe.domain.batch.entity.AdminContentUpdateJob;
import com.example.udtbe.domain.batch.entity.enums.BatchStatus;
import com.example.udtbe.domain.batch.repository.AdminContentDeleteJobRepository;
import com.example.udtbe.domain.batch.repository.AdminContentRegisterJobRepository;
import com.example.udtbe.domain.batch.repository.AdminContentUpdateJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchSkipListener implements SkipListener<Object, Object> {

    private final AdminContentRegisterJobRepository adminContentRegisterJobRepository;
    private final AdminContentUpdateJobRepository adminContentUpdateJobRepository;
    private final AdminContentDeleteJobRepository adminContentDeleteJobRepository;

    @Override
    public void onSkipInWrite(Object item, Throwable t) {
        log.error("🚨 SKIP LISTENER CALLED - onSkipInWrite: item={}, exception={}", item,
                t.getMessage());

        if (item instanceof AdminContentRegisterJob registerJob) {
            registerJob.changeStatus(BatchStatus.FAILED);
            adminContentRegisterJobRepository.save(registerJob);
        } else if (item instanceof AdminContentUpdateJob updateJob) {
            updateJob.changeStatus(BatchStatus.FAILED);
            adminContentUpdateJobRepository.save(updateJob);
        } else if (item instanceof AdminContentDeleteJob deleteJob) {
            deleteJob.changeStatus(BatchStatus.FAILED);
            adminContentDeleteJobRepository.save(deleteJob);
        }
    }

    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        log.warn("Item skipped in process phase: {}, reason: {}", item, t.getMessage());

        if (item instanceof AdminContentRegisterJob registerJob) {
            registerJob.changeStatus(BatchStatus.FAILED);
            adminContentRegisterJobRepository.save(registerJob);
        } else if (item instanceof AdminContentUpdateJob updateJob) {
            updateJob.changeStatus(BatchStatus.FAILED);
            adminContentUpdateJobRepository.save(updateJob);
        } else if (item instanceof AdminContentDeleteJob deleteJob) {
            deleteJob.changeStatus(BatchStatus.FAILED);
            adminContentDeleteJobRepository.save(deleteJob);
        }
    }
}