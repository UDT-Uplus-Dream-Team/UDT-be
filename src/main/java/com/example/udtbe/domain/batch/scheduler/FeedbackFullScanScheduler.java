package com.example.udtbe.domain.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        final String deleteSql = "TRUNCATE TABLE feedback_statistics";

        final String insertSql =
                "INSERT INTO feedback_statistics " +
                        "  (member_id, genre_type, like_count, dislike_count, uninterested_count, is_deleted) "
                        +
                        "SELECT " +
                        "  f.member_id, " +
                        "  g.genre_type, " +
                        "  SUM(CASE WHEN f.feedback_type = 'LIKE' THEN 1 ELSE 0 END), " +
                        "  SUM(CASE WHEN f.feedback_type = 'DISLIKE' THEN 1 ELSE 0 END), " +
                        "  SUM(CASE WHEN f.feedback_type = 'UNINTERESTED' THEN 1 ELSE 0 END), " +
                        "  FALSE " +
                        "FROM feedback f " +
                        "  JOIN content_genre cg ON f.content_id = cg.content_id " +
                        "  JOIN genre g         ON cg.genre_id = g.genre_id " +
                        "WHERE f.is_deleted = FALSE " +
                        "  AND g.is_deleted = FALSE " +
                        "GROUP BY f.member_id, g.genre_type";
        try {
            jdbc.execute(deleteSql);
            jdbc.execute(insertSql);
            log.info("Feedback 통계 풀 스캔: 삭제 및 재삽입 성공");
        } catch (Exception ex) {
            log.error("풀 스캔 동기화 실패", ex);
        }
    }
}
