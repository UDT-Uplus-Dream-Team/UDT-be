package com.example.udtbe.domain.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackFullScanScheduler {

    private final JdbcTemplate jdbc;

    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    public void scheduleFullScan() {
        log.info("========================================");
        log.info("1) Feedback 통계 풀 스캔 동기화 시작");
        String sql = "" +
                "INSERT INTO feedback_statistics " +
                "(member_id, genre_type, like_count, dislike_count, uninterested_count, is_deleted) "
                +
                "SELECT f.member_id, cg.genre_type, " +
                "SUM(CASE WHEN f.feedback_type='LIKE' THEN 1 ELSE 0 END), " +
                "SUM(CASE WHEN f.feedback_type='DISLIKE' THEN 1 ELSE 0 END), " +
                "SUM(CASE WHEN f.feedback_type='UNINTERESTED' THEN 1 ELSE 0 END), FALSE " +
                "FROM feedback f " +
                "JOIN content_genre cg ON f.content_id = cg.content_id " +
                "GROUP BY f.member_id, cg.genre_type " +
                "ON DUPLICATE KEY UPDATE " +
                "like_count = VALUES(like_count), " +
                "dislike_count = VALUES(dislike_count), " +
                "uninterested_count = VALUES(uninterested_count), " +
                "is_deleted = FALSE";

        try {
            jdbc.execute(sql);
            log.info("2) Feedback 통계 풀 스캔 동기화 완료");
        } catch (TransientDataAccessException tdae) {
            log.warn("일시적 DB 오류로 풀 스캔 실패, 재시도 필요: {}", tdae.getMessage(), tdae);
        } catch (Exception ex) {
            log.error("풀 스캔 동기화 중 치명적 오류 발생", ex);
        }
    }
}
