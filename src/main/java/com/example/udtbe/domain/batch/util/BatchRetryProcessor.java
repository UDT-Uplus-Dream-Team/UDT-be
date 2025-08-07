package com.example.udtbe.domain.batch.util;

import com.example.udtbe.domain.batch.entity.AdminContentDeleteJob;
import com.example.udtbe.domain.batch.entity.AdminContentRegisterJob;
import com.example.udtbe.domain.batch.entity.AdminContentUpdateJob;
import com.example.udtbe.domain.batch.entity.enums.BatchStatus;
import com.example.udtbe.domain.batch.listener.StepStatsListener;
import com.example.udtbe.global.exception.RestApiException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchRetryProcessor {

    private static final int RETRY_LIMIT = 3;

    private final StepStatsListener stepStatsListener;
    private final ThreadLocal<AtomicBoolean> systemFailureDetected = ThreadLocal.withInitial(
            () -> new AtomicBoolean(false));

    public <T> boolean processWithRetry(T item, Runnable businessLogic, Consumer<T> saveItem) {
        if (systemFailureDetected.get().get()) {
            setItemStatus(item, BatchStatus.FAILED, "SYSTEM_FAILURE",
                    "Previous system failure detected");
            saveItem.accept(item);
            stepStatsListener.incrementSystemFailure();
            return false;
        }

        for (int attempt = 1; attempt <= RETRY_LIMIT; attempt++) {
            try {
                businessLogic.run();
                stepStatsListener.incrementSuccess();
                return true;
            } catch (RestApiException e) {
                setItemStatus(item, BatchStatus.INVALID, "SKIP_ERROR", e.getMessage());
                saveItem.accept(item);
                stepStatsListener.incrementSkip();
                return false;
            } catch (TransientDataAccessException e) {
                if (attempt == 1) {
                    setItemStatus(item, BatchStatus.RETRYING, "RETRY_ERROR", e.getMessage());
                }
                incrementRetryCount(item);
                stepStatsListener.incrementRetry();

                if (attempt == RETRY_LIMIT) {
                    systemFailureDetected.get().set(true);
                    setItemStatus(item, BatchStatus.FAILED, "RETRY_EXHAUSTED", e.getMessage());
                    saveItem.accept(item);
                    stepStatsListener.incrementFail();
                    return false;
                }
            } catch (Exception e) {
                setItemStatus(item, BatchStatus.FAILED, "OTHER_ERROR", e.getMessage());
                saveItem.accept(item);
                stepStatsListener.incrementFail();
                return false;
            }
        }
        return false;
    }

    private <T> void setItemStatus(T item, BatchStatus status, String errorCode,
            String errorMessage) {
        log.info("{}", status.name());
        if (item instanceof AdminContentRegisterJob registerJob) {
            registerJob.changeStatus(status);
            registerJob.finish();
            if (errorCode != null) {
                registerJob.setError(errorCode, errorMessage);
            }
        } else if (item instanceof AdminContentUpdateJob updateJob) {
            updateJob.changeStatus(status);
            updateJob.finish();
            if (errorCode != null) {
                updateJob.setError(errorCode, errorMessage);
            }
        } else if (item instanceof AdminContentDeleteJob deleteJob) {
            deleteJob.changeStatus(status);
            deleteJob.finish();
            if (errorCode != null) {
                deleteJob.setError(errorCode, errorMessage);
            }
        }
    }

    private <T> void incrementRetryCount(T item) {
        if (item instanceof AdminContentRegisterJob registerJob) {
            registerJob.incrementRetryCount();
        } else if (item instanceof AdminContentUpdateJob updateJob) {
            updateJob.incrementRetryCount();
        } else if (item instanceof AdminContentDeleteJob deleteJob) {
            deleteJob.incrementRetryCount();
        }
    }

    public void resetSystemFailure() {
        systemFailureDetected.get().set(false);
    }
}