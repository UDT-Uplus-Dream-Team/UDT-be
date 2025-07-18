package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Feedback;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepository extends JpaRepository<Feedback, Long>, FeedbackQueryDSL {

    Optional<Feedback> findFeedbackById(Long feedbackId);

    List<Feedback> findByMemberIdAndIsDeletedFalse(Long memberId);

    @Query("""
            SELECT c
            FROM Feedback f
            JOIN f.content c
            WHERE f.isDeleted = false
            GROUP BY c
            ORDER BY
              SUM(CASE WHEN f.feedbackType = 'LIKE'  THEN 1
                       WHEN f.feedbackType = 'DISLIKE' THEN -1
                       ELSE 0 END) DESC,
              c.id ASC
            """)
    List<Content> findTopRankedContents(Pageable pageable);
}
