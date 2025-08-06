package com.example.udtbe.domain.batch.repository;

import com.example.udtbe.domain.admin.dto.common.BatchJobMetricDTO;
import com.example.udtbe.domain.admin.dto.response.AdminScheduledContentMetricGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminScheduledContentResponse;
import com.example.udtbe.domain.admin.dto.response.AdminScheduledContentResultGetResponse;
import com.example.udtbe.domain.batch.entity.enums.BatchFilterType;
import com.example.udtbe.global.dto.CursorPageResponse;

public interface AdminContentJobRepositoryCustom {

    CursorPageResponse<AdminScheduledContentResponse> getJobsByCursor(
            String cursor, int size, BatchFilterType type);

    BatchJobMetricDTO getContentRegisterJobMetrics(Long metricId);

    BatchJobMetricDTO getContentUpdateJobMetrics(Long metricId);

    BatchJobMetricDTO getContentDeleteJobMetrics(Long metricId);

    AdminScheduledContentMetricGetResponse getScheduledContentMetrics();

    CursorPageResponse<AdminScheduledContentResultGetResponse> getScheduledContentResults(
            String cursor, int size);
}
