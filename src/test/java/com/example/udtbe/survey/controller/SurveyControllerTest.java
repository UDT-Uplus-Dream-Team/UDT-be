package com.example.udtbe.survey.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.ContentMetadataFixture;
import com.example.udtbe.common.support.ApiSupport;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.repository.ContentMetadataRepository;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.domain.member.repository.MemberRepository;
import com.example.udtbe.domain.survey.controller.SurveyController;
import com.example.udtbe.domain.survey.dto.SurveyMapper;
import com.example.udtbe.domain.survey.dto.request.SurveyCreateRequest;
import com.example.udtbe.domain.survey.repository.SurveyRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SurveyControllerTest extends ApiSupport {

    @Autowired
    SurveyController surveyController;

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    ContentMetadataRepository contentMetadataRepository;

    @AfterEach
    void tearDown() {
        surveyRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @DisplayName("설문조사를 저장한다.")
    @Test
    void createSurvey() throws Exception {
        // given
        Content savedContent = contentRepository.save(ContentFixture.content("드라마", "드라마입니다."));
        contentMetadataRepository.save(ContentMetadataFixture.dramaMetadata(savedContent));

        List<String> platforms = List.of("넷플릭스", "디즈니+");
        List<String> genres = List.of("코미디", "범죄");
        List<Long> contentIds = List.of(savedContent.getId());

        SurveyCreateRequest request = new SurveyCreateRequest(platforms, genres, contentIds);

        // when  // then
        mockMvc.perform(post("/api/survey")
                        .content(toJson(request))
                        .contentType(APPLICATION_JSON)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isNoContent());
    }

    @DisplayName("설문조사는 2회 이상 할 수 없다.")
    @Test
    void throwExceptionIfSurveyAlreadyExistsForMember() throws Exception {
        // given
        Content savedContent = contentRepository.save(ContentFixture.content("드라마", "드라마입니다."));
        ContentMetadata savedContentMetadata = contentMetadataRepository.save(
                ContentMetadataFixture.dramaMetadata(savedContent));

        List<String> platforms = List.of("넷플릭스", "디즈니+");
        List<String> genres = List.of("코미디", "범죄");
        List<Long> contentIds = List.of(savedContent.getId());

        SurveyCreateRequest request = new SurveyCreateRequest(platforms, genres, contentIds);

        surveyRepository.save(SurveyMapper.toEntity(
                request, loginTempMember, List.of(String.valueOf(savedContentMetadata.getId()))
        ));

        // when  // then
        mockMvc.perform(post("/api/survey")
                        .content(toJson(request))
                        .contentType(APPLICATION_JSON)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("SURVEY_ALREADY_EXISTS_FOR_MEMBER"))
                .andExpect(jsonPath("$.message").value("이미 설문조사를 완료한 회원입니다."));
    }

    @DisplayName("설문조사 시 OTT 플랫폼은 필수 값이다.")
    @Test
    void throwExceptionIfSurveyPlatformIsNull() throws Exception {
        // given
        Content savedContent = contentRepository.save(ContentFixture.content("드라마", "드라마입니다."));
        contentMetadataRepository.save(ContentMetadataFixture.dramaMetadata(savedContent));

        List<String> platforms = null;
        List<String> genres = List.of("코미디", "범죄");
        List<Long> contentIds = List.of(savedContent.getId());

        SurveyCreateRequest request = new SurveyCreateRequest(platforms, genres, contentIds);

        // when  // then
        mockMvc.perform(post("/api/survey")
                        .content(toJson(request))
                        .contentType(APPLICATION_JSON)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("플랫폼 리스트는 필수 값입니다."));
    }

    @DisplayName("설문조사 시 OTT 플랫폼의 최소 선택 갯수는 1개다.")
    @Test
    void throwExceptionIfSurveyPlatformCountIsLessThanOne() throws Exception {
        // given
        Content savedContent = contentRepository.save(ContentFixture.content("드라마", "드라마입니다."));
        contentMetadataRepository.save(ContentMetadataFixture.dramaMetadata(savedContent));

        List<String> platforms = Collections.emptyList();
        List<String> genres = List.of("코미디", "범죄");
        List<Long> contentIds = List.of(savedContent.getId());

        SurveyCreateRequest request = new SurveyCreateRequest(platforms, genres, contentIds);

        // when  // then
        mockMvc.perform(post("/api/survey")
                        .content(toJson(request))
                        .contentType(APPLICATION_JSON)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("OTT 플랫폼은 최소 1개 이상 최대 7개 이하입니다."));
    }

    @DisplayName("설문조사 시 OTT 플랫폼의 최대 선택 갯수는 7개다.")
    @Test
    void throwExceptionIfSurveyPlatformCountExceedsSeven() throws Exception {
        // given
        Content savedContent = contentRepository.save(ContentFixture.content("드라마", "드라마입니다."));
        contentMetadataRepository.save(ContentMetadataFixture.dramaMetadata(savedContent));

        List<String> platforms = List.of(
                "넷플릭스", "디즈니+", "티빙", "쿠팡플레이", "웨이브", "왓챠", "Apple TV", "유튜브"
        );
        List<String> genres = List.of("코미디", "범죄");
        List<Long> contentIds = List.of(savedContent.getId());

        SurveyCreateRequest request = new SurveyCreateRequest(platforms, genres, contentIds);

        // when  // then
        mockMvc.perform(post("/api/survey")
                        .content(toJson(request))
                        .contentType(APPLICATION_JSON)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("OTT 플랫폼은 최소 1개 이상 최대 7개 이하입니다."));
    }

    @DisplayName("설문조사 시 선호 장르는 필수 값이다.")
    @Test
    void throwExceptionIfSurveyGenreIsNull() throws Exception {
        // given
        Content savedContent = contentRepository.save(ContentFixture.content("드라마", "드라마입니다."));
        contentMetadataRepository.save(ContentMetadataFixture.dramaMetadata(savedContent));

        List<String> platforms = List.of("넷플릭스", "디즈니+");
        List<String> genres = null;
        List<Long> contentIds = List.of(savedContent.getId());

        SurveyCreateRequest request = new SurveyCreateRequest(platforms, genres, contentIds);

        // when  // then
        mockMvc.perform(post("/api/survey")
                        .content(toJson(request))
                        .contentType(APPLICATION_JSON)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("선호 장르는 필수 값입니다."));
    }

    @DisplayName("설문조사 시 선호 장르는 최소 1개 이상이다.")
    @Test
    void throwExceptionIfSurveyGenreCountIsLessThanOne() throws Exception {
        // given
        Content savedContent = contentRepository.save(ContentFixture.content("드라마", "드라마입니다."));
        contentMetadataRepository.save(ContentMetadataFixture.dramaMetadata(savedContent));

        List<String> platforms = List.of("넷플릭스", "디즈니+");
        List<String> genres = Collections.emptyList();
        List<Long> contentIds = List.of(savedContent.getId());

        SurveyCreateRequest request = new SurveyCreateRequest(platforms, genres, contentIds);

        // when  // then
        mockMvc.perform(post("/api/survey")
                        .content(toJson(request))
                        .contentType(APPLICATION_JSON)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("선호 장르는 최소 1개 이상 최대 3개 이하입니다."));
    }

    @DisplayName("설문조사 시 선호 장르는 최대 3개다.")
    @Test
    void throwExceptionIfSurveyGenreCountExceedsThree() throws Exception {
        // given
        Content savedContent = contentRepository.save(ContentFixture.content("드라마", "드라마입니다."));
        contentMetadataRepository.save(ContentMetadataFixture.dramaMetadata(savedContent));

        List<String> platforms = List.of("넷플릭스", "디즈니+");
        List<String> genres = List.of("코미디", "범죄", "액션", "뮤지컬");
        List<Long> contentIds = List.of(savedContent.getId());

        SurveyCreateRequest request = new SurveyCreateRequest(platforms, genres, contentIds);

        // when  // then
        mockMvc.perform(post("/api/survey")
                        .content(toJson(request))
                        .contentType(APPLICATION_JSON)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("선호 장르는 최소 1개 이상 최대 3개 이하입니다."));
    }

    @DisplayName("설문조사 시 보신 콘텐츠 선택하지 않을 수 있다.")
    @Test
    void createSurveyWithoutWatchedContents() throws Exception {
        // given
        List<String> platforms = List.of("넷플릭스", "디즈니+");
        List<String> genres = List.of("코미디", "범죄");
        List<Long> contentIds = Collections.emptyList();

        SurveyCreateRequest request = new SurveyCreateRequest(platforms, genres, contentIds);

        // when  // then
        mockMvc.perform(post("/api/survey")
                        .content(toJson(request))
                        .contentType(APPLICATION_JSON)
                        .cookie(accessTokenOfTempMember)
                )
                .andExpect(status().isNoContent());
    }
}
