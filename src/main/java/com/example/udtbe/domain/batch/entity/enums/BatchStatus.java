package com.example.udtbe.domain.batch.entity.enums;

import com.example.udtbe.global.exception.RestApiException;
import com.example.udtbe.global.exception.code.EnumErrorCode;
import java.util.Arrays;

public enum BatchStatus {
    PENDING, PROCESSING, COMPLETED, FAILED, SKIPPED, RETRYING, INVALID;

    public static BatchStatus from(String value) {
        return Arrays.stream(values())
                .filter(b -> b.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new RestApiException(EnumErrorCode.BATCH_JOB_TYPE_BAD_REQUEST));
    }
}
