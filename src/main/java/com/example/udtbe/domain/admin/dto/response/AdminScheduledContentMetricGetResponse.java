package com.example.udtbe.domain.admin.dto.response;

public record AdminScheduledContentMetricGetResponse(
        long totalRead,
        long totalCompleted,
        long totalInvalid,
        long totalFailed
) {

}
