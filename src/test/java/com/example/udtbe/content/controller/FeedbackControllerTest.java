package com.example.udtbe.content.controller;

import static org.mockito.Mockito.verify;

import com.example.udtbe.content.repository.FeedbackRepository;
import com.example.udtbe.domain.content.controller.FeedbackController;
import com.example.udtbe.domain.content.dto.request.BulkFeedbackRequestDto;
import com.example.udtbe.domain.content.dto.request.FeedbackRequestDto;
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

        Member mockMember = Member.of(
                "test@example.com", "test", Role.ROLE_USER, null,
                Gender.MAN, LocalDateTime.now(), false
        );

        // when
        feedbackController.saveFeedback(requestDto, mockMember);

        // then
        verify(feedbackService).saveFeedbacks(requestDto.feedbacks(), mockMember);

    }
}