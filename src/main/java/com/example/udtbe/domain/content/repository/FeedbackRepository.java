package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

}
