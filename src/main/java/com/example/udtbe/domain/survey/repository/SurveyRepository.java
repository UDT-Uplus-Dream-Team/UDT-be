package com.example.udtbe.domain.survey.repository;

import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.survey.entity.Survey;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {

    boolean existsByMember(Member member);
    Optional<Survey> findByMemberId(Long memberId);

}
