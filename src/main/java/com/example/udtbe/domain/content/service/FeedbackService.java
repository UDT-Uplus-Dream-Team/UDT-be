package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.dto.request.FeedbackRequestDto;
import com.example.udtbe.domain.content.dto.response.BulkFeedbackResponseDto;
import com.example.udtbe.domain.content.dto.response.FeedbackResponseDto;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.exception.ContentErrorCode;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.domain.content.repository.FeedbackRepository;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.exception.MemberErrorCode;
import com.example.udtbe.global.exception.RestApiException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final ContentQuery contentQuery;
    private final ContentRepository contentRepository;
    private final FeedbackRepository feedbackRepository;

    @Transactional
    public void saveFeedbacks(List<FeedbackRequestDto> requests, Member member) {
        List<Feedback> feedbacks = requests.stream().map(
                req -> {
                    Content content = contentRepository.findById(req.contentId())
                            .orElseThrow(
                                    () -> new RestApiException(ContentErrorCode.CONTENT_NOT_FOUND));

                    FeedbackType type = req.feedback() ? FeedbackType.LIKE : FeedbackType.DISLIKE;
                    return Feedback.of(type, false, member, content);
                }
        ).toList();
        feedbackRepository.saveAll(feedbacks);
    }

    public BulkFeedbackResponseDto getFeedbackList(Long cursor, int size, FeedbackType feedbackType,
            Member member) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));

        List<Feedback> feedbacks;

        if (cursor == null) {
            feedbacks = feedbackRepository.findTopByMemberAndFeedbackTypeOrderByIdDesc(member,
                    feedbackType, pageable);
        } else {
            feedbacks = feedbackRepository.findTopByMemberAndFeedbackTypeAndIdLessThanOrderByIdDesc(
                    member, feedbackType, cursor, pageable);
        }

        List<FeedbackResponseDto> dtoList = feedbacks.stream()
                .map(FeedbackResponseDto::from)
                .collect(Collectors.toList());

        Long nextCursor = feedbacks.isEmpty() ? null : feedbacks.get(feedbacks.size() - 1).getId();
        boolean hasNext = feedbacks.size() == size;

        return new BulkFeedbackResponseDto(dtoList, nextCursor, hasNext);
    }

    @Transactional
    public void deleteFeedback(Long feedbackId, Member member) {
        Feedback feedback = feedbackRepository.getFeedbackById(feedbackId)
                .orElseThrow(() -> new RestApiException(ContentErrorCode.CONTENT_NOT_FOUND));

        if (!feedback.getMember().getId().equals(member.getId())) {
            throw new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND);
        }

        feedback.softDeleted();
    }

}
