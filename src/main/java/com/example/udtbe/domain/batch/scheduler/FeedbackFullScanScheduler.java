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
        final String sql =
                "INSERT INTO feedback_statistics " +
                        "  (member_id, genre_type, like_count, dislike_count, uninterested_count, is_deleted) "
                        +
                        "SELECT " +
                        "  f.member_id, " +
                        "  g.genre_type, " +
                        "  SUM(CASE WHEN f.feedback_type='LIKE' THEN 1 ELSE 0 END), " +
                        "  SUM(CASE WHEN f.feedback_type='DISLIKE' THEN 1 ELSE 0 END), " +
                        "  SUM(CASE WHEN f.feedback_type='UNINTERESTED' THEN 1 ELSE 0 END), " +
                        "  FALSE " +
                        "FROM feedback f " +
                        "JOIN content_genre cg ON f.content_id = cg.content_id " +
                        "JOIN genre g ON cg.genre_id = g.genre_id " +
                        "GROUP BY f.member_id, g.genre_type " +
                        "ON DUPLICATE KEY UPDATE " +
                        "  like_count = VALUES(like_count), " +
                        "  dislike_count = VALUES(dislike_count), " +
                        "  uninterested_count = VALUES(uninterested_count), " +
                        "  is_deleted = FALSE;";

        try {
            jdbc.execute(sql);
            log.info("Feedback 통계 풀 스캔 동기화 성공");
        } catch (Exception ex) {
            log.error("풀 스캔 동기화 실패", ex);
        }
    }
}
