package com.example.udtbe.domain.admin.dto.response;

public record AdminContentDelJobGetDetailResponse(

        Long batchJobMetricId,

        Long contentId,

        String errorCode,

        String errorMessage,

        int retryCount,

        int skipCount

) {

}
