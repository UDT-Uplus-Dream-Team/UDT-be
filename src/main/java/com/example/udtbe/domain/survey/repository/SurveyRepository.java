package com.example.udtbe.domain.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.udtbe.entity.Survey;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
}
