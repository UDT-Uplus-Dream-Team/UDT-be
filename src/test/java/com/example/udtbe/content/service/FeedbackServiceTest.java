package com.example.udtbe.content.service;

import static com.example.udtbe.common.fixture.ContentFixture.content;
import static com.example.udtbe.common.fixture.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.domain.content.dto.common.FeedbackContentDTO;
import com.example.udtbe.domain.content.dto.common.FeedbackCreateDTO;
import com.example.udtbe.domain.content.dto.request.FeedbackContentGetRequest;
import com.example.udtbe.domain.content.dto.request.FeedbackCreateBulkRequest;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.repository.FeedbackRepository;
import com.example.udtbe.domain.content.service.FeedbackQuery;
import com.example.udtbe.domain.content.service.FeedbackService;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.entity.enums.Role;
import com.example.udtbe.global.dto.CursorPageResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private FeedbackQuery feedbackQuery;

    @InjectMocks
    private FeedbackService feedbackService;

    @DisplayName("회원은 피드백을 제출할 수 있다.")
    @Test
    void saveFeedback() {
        // given
        List<FeedbackCreateDTO> feedbacks = List.of(
                new FeedbackCreateDTO(1L, FeedbackType.LIKE),
                new FeedbackCreateDTO(2L, FeedbackType.DISLIKE)
        );

        FeedbackCreateBulkRequest requestDto = new FeedbackCreateBulkRequest(feedbacks);

        Member member = member("test@example.com", Role.ROLE_USER);

        given(feedbackQuery.findContentById(1L)).willReturn(content("content1", "description1"));
        given(feedbackQuery.findContentById(2L)).willReturn(content("content2", "description2"));

        // when
        feedbackService.saveFeedbacks(requestDto.feedbacks(), member);

        // then
        verify(feedbackRepository).saveAll(anyList());
        verify(feedbackRepository, times(1)).saveAll(anyList());

    }

    @DisplayName("삭제된 피드백이 다시 저장되면 새로 남긴 피드백으로 저장된다.")
    @Test
    void saveFeedbacks_restoreDeletedFeedback() {
        // given
        Member member = MemberFixture.member("user@email.com", Role.ROLE_USER);
        Content content = ContentFixture.content("title", "description");
        ReflectionTestUtils.setField(content, "id", 1L);

        Feedback feedback = Feedback.of(FeedbackType.LIKE, true, member, content);

        FeedbackCreateDTO request = new FeedbackCreateDTO(content.getId(), FeedbackType.DISLIKE);

        given(feedbackQuery.findContentById(1L)).willReturn(content);
        given(feedbackQuery.findFeedbackByMemberIdAndContentId(member.getId(),
                content.getId())).willReturn(Optional.ofNullable(feedback));

        // when
        feedbackService.saveFeedbacks(List.of(request), member);

        // then
        ArgumentCaptor<List<Feedback>> captor = ArgumentCaptor.forClass(List.class);
        then(feedbackRepository).should().saveAll(captor.capture());

        Feedback savedFeedback = captor.getValue().get(0);
        assertThat(savedFeedback.getFeedbackType()).isEqualTo(FeedbackType.DISLIKE);
        assertThat(savedFeedback.isDeleted()).isFalse();
    }

    @DisplayName("이미 작성된 피드백이 존재하면 현재 남긴 피드백으로 업데이트된다.")
    @Test
    void saveFeedbacks_AlreadyExists() {
        // given
        Member member = MemberFixture.member("test@email.com", Role.ROLE_USER);
        Content content = ContentFixture.content("title", "description");
        ReflectionTestUtils.setField(content, "id", 1L);

        Feedback savedFeedback = Feedback.of(FeedbackType.LIKE, false, member, content);
        FeedbackCreateDTO request = new FeedbackCreateDTO(1L, FeedbackType.DISLIKE);

        given(feedbackQuery.findContentById(1L)).willReturn(content);
        given(feedbackQuery.findFeedbackByMemberIdAndContentId(member.getId(), content.getId()))
                .willReturn(Optional.ofNullable(savedFeedback));

        // when
        feedbackService.saveFeedbacks(List.of(request), member);

        // then
        assertThat(savedFeedback.getFeedbackType()).isEqualTo(FeedbackType.DISLIKE);
        verify(feedbackRepository, times(1)).saveAll(anyList());
    }


    @DisplayName("회원은 피드백들을 무한스크롤로 조회할 수 있다.")
    @Test
    void getFeedbacks() {
        // given
        Member member = member("test@example.com", Role.ROLE_USER);

        Content content1 = content("test_content1", "description11");
        Content content2 = content("test_content2", "description2");
        Content content3 = content("test_content3", "description3");

        Feedback feedback1 = Feedback.of(
                FeedbackType.LIKE, false, member, content1
        );

        Feedback feedback2 = Feedback.of(
                FeedbackType.LIKE, false, member, content2
        );

        Feedback feedback3 = Feedback.of(
                FeedbackType.LIKE, false, member, content3
        );

        FeedbackContentGetRequest request = new FeedbackContentGetRequest(
                null, 2, "LIKE", "NEWEST"
        );

        List<Feedback> feedbacks = List.of(feedback1, feedback2, feedback3);

        given(feedbackQuery.getFeedbacksByCursor(member, request)).willReturn(feedbacks);

        // when
        CursorPageResponse<FeedbackContentDTO> result
                = feedbackService.getFeedbackList(request, member);

        // then
        assertThat(result.item()).hasSize(2);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursor()).isEqualTo(feedbacks.get(1).getId());
    }

    @DisplayName("회원은 피드백을 삭제할 수 있다.")
    @Test
    void deleteFeedback() {
        // given
        Member member = member("test@example.com", Role.ROLE_USER);

        Content content = content("test_content", "description");

        Feedback feedback = Feedback.of(FeedbackType.LIKE, false, member, content);

        ReflectionTestUtils.setField(feedback, "id", 1L);
        ReflectionTestUtils.setField(member, "id", 10L);

        given(feedbackQuery.findFeedbackById(1L)).willReturn(feedback);

        // when
        feedbackService.deleteFeedback(1L, member);

        // then
        assertThat(feedback.isDeleted()).isTrue();
    }
}