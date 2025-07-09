package com.example.udtbe.domain.survey.controller;

import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.survey.dto.request.SurveyCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "설문조사 API", description = "설문조사 관련 API")
public interface SurveyControllerApiSpec {

    @Operation(summary = "설문조사 API", description = "콘텐츠 추천 기반 설문조사를 한다.")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping("/api/survey")
    public ResponseEntity<Void> survey(
            @RequestBody @Valid SurveyCreateRequest request,
            @AuthenticationPrincipal Member member
    );
}
