package com.example.udtbe.content.service;

import static com.example.udtbe.common.fixture.ContentFixture.content;
import static com.example.udtbe.common.fixture.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.udtbe.domain.content.dto.request.BulkFeedbackRequestDto;
import com.example.udtbe.domain.content.dto.request.FeedbackRequestDto;
import com.example.udtbe.domain.content.dto.response.BulkFeedbackResponseDto;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.domain.content.repository.FeedbackRepository;
import com.example.udtbe.domain.content.service.FeedbackService;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.entity.enums.Gender;
import com.example.udtbe.domain.member.entity.enums.Role;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private ContentRepository contentRepository;

    @InjectMocks
    private FeedbackService feedbackService;

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

        when(contentRepository.findById(1L)).thenReturn(Optional.of(content("content1")));
        when(contentRepository.findById(2L)).thenReturn(Optional.of(content("content2")));

        // when
        feedbackService.saveFeedbacks(requestDto.feedbacks(), member);

        // then
        verify(feedbackRepository).saveAll(anyList());

    }

    @DisplayName("회원은 좋아요한 피드백들을 조회할 수 있다.")
    @Test
    void getFeedbacks() {
        // given
        Member member = member("test@example.com", Role.ROLE_USER);

        Content content1 = content("test_content1");
        Content content2 = content("test_content2");
        Content content3 = content("test_content3");

        Feedback feedback1 = Feedback.of(
                FeedbackType.LIKE, false, member, content1
        );

        Feedback feedback2 = Feedback.of(
                FeedbackType.DISLIKE, false, member, content2
        );

        Feedback feedback3 = Feedback.of(
                FeedbackType.LIKE, false, member, content3
        );

        List<Feedback> feedbacks = List.of(feedback1, feedback3);

        int size = 3;

        // when
        when(feedbackRepository.findTopByMemberAndFeedbackTypeOrderByIdDesc(
                eq(member), eq(FeedbackType.LIKE), any(Pageable.class)))
                .thenReturn(feedbacks);

        BulkFeedbackResponseDto result = feedbackService.getFeedbackList(
                null, size, FeedbackType.LIKE, member
        );

        // then
        assertThat(result.feedbacks()).hasSize(2);
        assertThat(result.hasNext()).isFalse();

    }

    @DisplayName("회원은 피드백을 삭제할 수 있다.")
    @Test
    void deleteFeedback() {
        // given
        Member member = member("test@example.com", Role.ROLE_USER);

        Content content = content("test_content");

        Feedback feedback = Feedback.of(FeedbackType.LIKE, false, member, content);

        // when
        feedback.softDeleted();

        // then
        assertThat(feedback.isDeleted()).isTrue();
    }
}