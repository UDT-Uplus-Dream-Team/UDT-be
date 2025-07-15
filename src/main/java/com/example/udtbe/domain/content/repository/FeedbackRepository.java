package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.Feedback;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long>, FeedbackQueryDSL {

    Optional<Feedback> findFeedbackById(Long feedbackId);

    List<Feedback> findByMemberIdAndIsDeletedFalse(Long memberId);
}
