package com.example.udtbe.content.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.example.udtbe.content.repository.FeedbackRepository;
import com.example.udtbe.domain.content.controller.FeedbackController;
import com.example.udtbe.domain.content.dto.request.BulkFeedbackRequestDto;
import com.example.udtbe.domain.content.dto.request.FeedbackRequestDto;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.domain.content.service.FeedbackService;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.entity.enums.Gender;
import com.example.udtbe.domain.member.entity.enums.Role;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FeedbackControllerTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private FeedbackController feedbackController;

    @DisplayName("회원은 피드백을 제출할 수 있다.")
    @Test
    void saveFeedback() {
        // given
        List<FeedbackRequestDto> feedbacks = List.of(
                new FeedbackRequestDto(1L, true),
                new FeedbackRequestDto(2L, false)
        );

        BulkFeedbackRequestDto requestDto = new BulkFeedbackRequestDto(feedbacks);

        Member member = Member.of(
                "test@example.com", "test", Role.ROLE_USER, null,
                Gender.MAN, LocalDateTime.now(), false
        );

        // when
        feedbackController.saveFeedback(requestDto, member);

        // then
        verify(feedbackService).saveFeedbacks(requestDto.feedbacks(), member);

    }

    @DisplayName("회원은 피드백을 삭제할 수 있다.")
    @Test
    void deleteFeedback() {
        // given
        Member member = Member.of(
                "test@example.com", "test", Role.ROLE_USER, null,
                Gender.MAN, LocalDateTime.now(), false
        );

        Content content = Content.of(
                "test", "test_description", "www.https://poster-url/", "www.https://backdrop-url/",
                "www.https://trailer-url/", LocalDateTime.now(),
                120, 0, null, false, List.of(), List.of(), List.of(), List.of(), List.of());

        Feedback feedback = Feedback.of(
                FeedbackType.LIKE, false, member, content
        );

        // when
        feedback.softDeleted();

        // then
        assertThat(feedback.isDeleted()).isTrue();
    }
}