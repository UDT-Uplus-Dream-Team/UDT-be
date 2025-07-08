package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Optional<Feedback> getFeedbackById(Long feedbackId);

    List<Feedback> findTopByMemberAndFeedbackTypeOrderByIdDesc(Member member,
            FeedbackType feedbackType, Pageable pageable);

    List<Feedback> findTopByMemberAndFeedbackTypeAndIdLessThanOrderByIdDesc(
            Member member, FeedbackType feedbackType, Long cursor, Pageable pageable);

}
