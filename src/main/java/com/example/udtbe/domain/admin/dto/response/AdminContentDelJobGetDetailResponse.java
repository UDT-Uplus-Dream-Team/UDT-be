package com.example.udtbe.domain.admin.dto.response;

import com.example.udtbe.domain.batch.entity.enums.BatchStatus;

public record AdminContentDelJobGetDetailResponse(

        Long batchJobMetricId,

        BatchStatus status,

        Long contentId,

        String errorCode,

        String errorMessage,

        int retryCount,

        int skipCount

) {

}
