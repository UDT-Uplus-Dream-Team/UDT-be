package com.example.udtbe.member.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.udtbe.common.support.ApiSupport;
import com.example.udtbe.domain.member.dto.request.MemberUpdateGenreRequest;
import com.example.udtbe.domain.member.dto.request.MemberUpdatePlatformRequest;
import com.example.udtbe.domain.member.repository.MemberRepository;
import com.example.udtbe.domain.survey.dto.request.SurveyCreateRequest;
import com.example.udtbe.domain.survey.repository.SurveyRepository;
import com.example.udtbe.global.exception.code.EnumErrorCode;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("[MemberController] 통합테스트")
class MemberControllerTest extends ApiSupport {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    SurveyRepository surveyRepository;

    @AfterEach
    void tearDown() {
        surveyRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @DisplayName("마이페이지에서 회원 선호 장르를 수정한다.")
    @Test
    void updateGenre() throws Exception {
        // given
        List<String> platforms = List.of("넷플릭스", "디즈니+");
        List<String> genres = List.of("코미디");
        List<Long> contentIds = Collections.emptyList();

        SurveyCreateRequest surveyCreateRequest = new SurveyCreateRequest(
                platforms,
                genres,
                contentIds
        );

        mockMvc.perform(post("/api/survey")
                .content(toJson(surveyCreateRequest))
                .contentType(APPLICATION_JSON)
                .cookie(accessTokenOfTempMember)
        );

        MemberUpdateGenreRequest memberUpdateGenreRequest = new MemberUpdateGenreRequest(
                List.of("서사/드라마", "키즈")
        );

        ResultActions actions = mockMvc.perform(patch("/api/users/survey/genre")
                .content(toJson(memberUpdateGenreRequest))
                .contentType(APPLICATION_JSON)
                .cookie(accessTokenOfTempMember)
        ).andExpect(status().isOk());

        for (int i = 0; i < memberUpdateGenreRequest.genres().size(); i++) {
            actions.andExpect(jsonPath("$.genres[" + i + "]").value(
                    memberUpdateGenreRequest.genres().get(i)));
        }
    }

    @DisplayName("마이페이지에서 올바르지 않은 장르타입으로 수정 시 400에러가 나온다.")
    @Test
    void updateInvalidGenre() throws Exception {
        // given
        List<String> platforms = List.of("넷플릭스", "디즈니+");
        List<String> genres = List.of("코미디");
        List<Long> contentIds = Collections.emptyList();

        SurveyCreateRequest surveyCreateRequest = new SurveyCreateRequest(
                platforms,
                genres,
                contentIds
        );

        mockMvc.perform(post("/api/survey")
                .content(toJson(surveyCreateRequest))
                .contentType(APPLICATION_JSON)
                .cookie(accessTokenOfTempMember)
        );

        MemberUpdateGenreRequest memberUpdateGenreRequest = new MemberUpdateGenreRequest(
                List.of("할래말래", "키즈", "aaaa")
        );

        // when & then
        mockMvc.perform(patch("/api/users/survey/genre")
                        .content(toJson(memberUpdateGenreRequest))
                        .contentType(APPLICATION_JSON)
                        .cookie(accessTokenOfTempMember)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(
                        EnumErrorCode.GENRE_TYPE_BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(
                        EnumErrorCode.GENRE_TYPE_BAD_REQUEST.getMessage()));
    }

    @DisplayName("마이페이지에서 장르를 선택하지 않을 시 404에러가 나온다.")
    @Test
    void updateZeroGenre() throws Exception {
        List<String> platforms = List.of("넷플릭스", "디즈니+");
        List<String> genres = List.of("코미디");
        List<Long> contentIds = Collections.emptyList();

        SurveyCreateRequest surveyCreateRequest = new SurveyCreateRequest(
                platforms,
                genres,
                contentIds
        );

        mockMvc.perform(post("/api/survey")
                .content(toJson(surveyCreateRequest))
                .contentType(APPLICATION_JSON)
                .cookie(accessTokenOfTempMember)
        );

        MemberUpdateGenreRequest memberUpdateGenreRequest = new MemberUpdateGenreRequest(
                List.of()
        );

        // when & then
        mockMvc.perform(patch("/api/users/survey/genre")
                        .content(toJson(memberUpdateGenreRequest))
                        .contentType(APPLICATION_JSON)
                        .cookie(accessTokenOfTempMember)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("장르 수정은 최소 1개 이상 최대 3개 이하입니다."));
    }

    @DisplayName("마이페이지에서 구독 플렛폼을 수정할 수 있다.")
    @Test
    void updatePlatform() throws Exception {
        // given
        List<String> platforms = List.of("넷플릭스", "디즈니+");
        List<String> genres = List.of("코미디");
        List<Long> contentIds = Collections.emptyList();

        SurveyCreateRequest surveyCreateRequest = new SurveyCreateRequest(
                platforms,
                genres,
                contentIds
        );

        mockMvc.perform(post("/api/survey")
                .content(toJson(surveyCreateRequest))
                .contentType(APPLICATION_JSON)
                .cookie(accessTokenOfTempMember)
        );

        MemberUpdatePlatformRequest memberUpdatePlatformRequest = new MemberUpdatePlatformRequest(
                List.of("왓챠", "티빙")
        );

        ResultActions actions = mockMvc.perform(patch("/api/users/survey/platform")
                .content(toJson(memberUpdatePlatformRequest))
                .contentType(APPLICATION_JSON)
                .cookie(accessTokenOfTempMember)
        ).andExpect(status().isOk());

        for (int i = 0; i < memberUpdatePlatformRequest.platforms().size(); i++) {
            actions.andExpect(jsonPath("$.platforms[" + i + "]").value(
                    memberUpdatePlatformRequest.platforms().get(i)));
        }
    }

    @DisplayName("마이페이지에서 올바르지 않은 플렛폼 타입으로 수정 시 400에러가 나온다.")
    @Test
    void updateInvalidPlatform() throws Exception {
        // given
        List<String> platforms = List.of("넷플릭스", "디즈니+");
        List<String> genres = List.of("코미디");
        List<Long> contentIds = Collections.emptyList();

        SurveyCreateRequest surveyCreateRequest = new SurveyCreateRequest(
                platforms,
                genres,
                contentIds
        );

        mockMvc.perform(post("/api/survey")
                .content(toJson(surveyCreateRequest))
                .contentType(APPLICATION_JSON)
                .cookie(accessTokenOfTempMember)
        );

        MemberUpdatePlatformRequest memberUpdatePlatformRequest = new MemberUpdatePlatformRequest(
                List.of("!!!", "??", "aaaa")
        );

        // when & then
        mockMvc.perform(patch("/api/users/survey/platform")
                        .content(toJson(memberUpdatePlatformRequest))
                        .contentType(APPLICATION_JSON)
                        .cookie(accessTokenOfTempMember)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(
                        EnumErrorCode.PLATFORM_TYPE_BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(
                        EnumErrorCode.PLATFORM_TYPE_BAD_REQUEST.getMessage()));
    }

}
