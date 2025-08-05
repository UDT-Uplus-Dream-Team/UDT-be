package com.example.udtbe.domain.batch.repository;

import static com.example.udtbe.domain.batch.entity.QAdminContentDeleteJob.adminContentDeleteJob;
import static com.example.udtbe.domain.batch.entity.QAdminContentRegisterJob.adminContentRegisterJob;
import static com.example.udtbe.domain.batch.entity.QAdminContentUpdateJob.adminContentUpdateJob;

import com.example.udtbe.domain.admin.dto.common.BatchJobMetricDTO;
import com.example.udtbe.domain.admin.dto.response.AdminScheduledContentResponse;
import com.example.udtbe.domain.batch.entity.enums.BatchFilterType;
import com.example.udtbe.domain.batch.entity.enums.BatchJobType;
import com.example.udtbe.domain.batch.entity.enums.BatchStatus;
import com.example.udtbe.domain.batch.exception.BatchErrorCode;
import com.example.udtbe.global.dto.CursorPageResponse;
import com.example.udtbe.global.exception.RestApiException;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AdminContentJobRepositoryImpl implements AdminContentJobRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    private final EntityManager em;

    @Override
    public CursorPageResponse<AdminScheduledContentResponse> getJobsByCursor(String cursor,
            int size,
            BatchFilterType type) {

        long jobId = Long.MAX_VALUE;
        LocalDateTime createdAt = LocalDateTime.now();
        String jobType = BatchJobType.REGISTER.name();

        if (StringUtils.hasText(cursor)) {
            String[] parts = cursor.split("\\|");
            if (parts.length == 3) {
                jobId = Long.parseLong(parts[0]);
                createdAt = LocalDateTime.parse(parts[1]);
                jobType = parts[2];
            } else {
                throw new RestApiException(BatchErrorCode.CURSOR_BAD_REQUEST);
            }
        }

        String statusCondition = "";
        if (BatchFilterType.FAILED.equals(type)) {
            statusCondition = "status = 'FAILED' AND\n";
        } else if (BatchFilterType.PENDING.equals(type)) {
            statusCondition = "status = 'PENDING'  AND\n";
        } else if (BatchFilterType.INVALID.equals(type)) {
            statusCondition = "status = 'INVALID'  AND\n";
        }

        String sql = """
                SELECT id, status, member_id, created_at, scheduled_at, finished_at, job_type
                FROM (
                    SELECT admin_content_register_job_id AS id, status, member_id, created_at, scheduled_at, finished_at, 'REGISTER' AS job_type
                    FROM admin_content_register_job
                    UNION ALL
                    SELECT admin_content_update_job_id AS id, status, member_id, created_at, scheduled_at, finished_at, 'UPDATE' AS job_type
                    FROM admin_content_update_job
                    UNION ALL
                    SELECT admin_content_delete_job_id AS id, status, member_id, created_at, scheduled_at, finished_at, 'DELETE' AS job_type
                    FROM admin_content_delete_job
                ) AS jobs
                WHERE (
                """ + statusCondition + """
                    (
                        created_at < :createdAt
                        OR (created_at = :createdAt AND job_type < :jobType)
                        OR (created_at = :createdAt AND job_type = :jobType AND id < :jobId)
                    )
                )
                ORDER BY created_at DESC,
                    CASE job_type
                    WHEN 'REGISTER' THEN 3
                    WHEN 'UPDATE' THEN 2
                    WHEN 'DELETE' THEN 1
                    ELSE 0
                    END DESC,
                    id DESC
                LIMIT :limit ;
                """;

        List<Object[]> resultList = em.createNativeQuery(sql)
                .setParameter("createdAt", createdAt)
                .setParameter("jobType", jobType)
                .setParameter("jobId", jobId)
                .setParameter("limit", size + 1)
                .getResultList();

        List<AdminScheduledContentResponse> results = resultList.stream()
                .map(row -> new AdminScheduledContentResponse(
                        ((Number) row[0]).longValue(),
                        BatchStatus.from((String) row[1]),
                        ((Number) row[2]).longValue(),
                        ((Timestamp) row[3]).toLocalDateTime(),
                        ((Timestamp) row[4]).toLocalDateTime(),
                        ((row[5] != null) ? ((Timestamp) row[5]).toLocalDateTime() : null),
                        BatchJobType.from((String) row[6])
                )).toList();

        String nextCursor = null;
        if (results.size() > size) {
            AdminScheduledContentResponse last = results.get(size - 1);
            nextCursor = String.format("%d|%s|%s",
                    last.id(),
                    last.createdAt(),
                    last.jobType());
        }

        return new CursorPageResponse<>(results, nextCursor, results.size() < size + 1);
    }

    @Override
    public BatchJobMetricDTO getContentRegisterJobMetrics(Long metricId) {

        List<Tuple> counts = jpaQueryFactory
                .select(adminContentRegisterJob.status, adminContentRegisterJob.id.count())
                .from(adminContentRegisterJob)
                .where(adminContentRegisterJob.status.in(
                        BatchStatus.COMPLETED,
                        BatchStatus.FAILED,
                        BatchStatus.INVALID).and(
                        adminContentRegisterJob.batchJobMetricId.eq(metricId)))
                .groupBy(adminContentRegisterJob.status)
                .fetch();

        Map<BatchStatus, Long> countMap = counts.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(adminContentRegisterJob.status),
                        tuple -> Optional.ofNullable(tuple.get(adminContentRegisterJob.id.count()))
                                .orElse(0L)
                ));

        long totalCompleted = countMap.getOrDefault(BatchStatus.COMPLETED, 0L);
        long totalFailed = countMap.getOrDefault(BatchStatus.FAILED, 0L);
        long totalInvalid = countMap.getOrDefault(BatchStatus.INVALID, 0L);
        long totalRead = totalCompleted + totalFailed + totalInvalid;

        return new BatchJobMetricDTO(totalRead, totalCompleted, totalInvalid, totalFailed);
    }


    @Override
    public BatchJobMetricDTO getContentUpdateJobMetrics(Long metricId) {

        List<Tuple> counts = jpaQueryFactory
                .select(adminContentUpdateJob.status, adminContentUpdateJob.id.count())
                .from(adminContentUpdateJob)
                .where(adminContentUpdateJob.status.in(
                        BatchStatus.COMPLETED,
                        BatchStatus.FAILED,
                        BatchStatus.INVALID).and(
                        adminContentUpdateJob.batchJobMetricId.eq(metricId)))
                .groupBy(adminContentUpdateJob.status)
                .fetch();

        Map<BatchStatus, Long> countMap = counts.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(adminContentUpdateJob.status),
                        tuple -> Optional.ofNullable(tuple.get(adminContentUpdateJob.id.count()))
                                .orElse(0L)
                ));

        long totalCompleted = countMap.getOrDefault(BatchStatus.COMPLETED, 0L);
        long totalFailed = countMap.getOrDefault(BatchStatus.FAILED, 0L);
        long totalInvalid = countMap.getOrDefault(BatchStatus.INVALID, 0L);
        long totalRead = totalCompleted + totalFailed + totalInvalid;

        return new BatchJobMetricDTO(totalRead, totalCompleted, totalInvalid, totalFailed);
    }

    @Override
    public BatchJobMetricDTO getContentDeleteJobMetrics(Long metricId) {

        List<Tuple> counts = jpaQueryFactory
                .select(adminContentDeleteJob.status, adminContentDeleteJob.id.count())
                .from(adminContentDeleteJob)
                .where(adminContentDeleteJob.status.in(
                        BatchStatus.COMPLETED,
                        BatchStatus.FAILED,
                        BatchStatus.INVALID).and(
                        adminContentDeleteJob.batchJobMetricId.eq(metricId)))
                .groupBy(adminContentDeleteJob.status)
                .fetch();

        Map<BatchStatus, Long> countMap = counts.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(adminContentDeleteJob.status),
                        tuple -> Optional.ofNullable(tuple.get(adminContentDeleteJob.id.count()))
                                .orElse(0L)
                ));

        long totalCompleted = countMap.getOrDefault(BatchStatus.COMPLETED, 0L);
        long totalFailed = countMap.getOrDefault(BatchStatus.FAILED, 0L);
        long totalInvalid = countMap.getOrDefault(BatchStatus.INVALID, 0L);
        long totalRead = totalCompleted + totalFailed + totalInvalid;

        return new BatchJobMetricDTO(totalRead, totalCompleted, totalInvalid, totalFailed);
    }
    
}