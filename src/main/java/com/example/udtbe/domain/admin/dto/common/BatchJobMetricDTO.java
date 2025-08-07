package com.example.udtbe.domain.admin.dto.common;

public record BatchJobMetricDTO(
        long totalRead,
        long totalCompleted,
        long totalInvalid,
        long totalFailed
) {

}
