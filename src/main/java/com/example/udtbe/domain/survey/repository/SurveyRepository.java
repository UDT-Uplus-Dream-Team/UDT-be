package com.example.udtbe.domain.survey.repository;

import com.example.udtbe.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

}
