package com.example.udtbe.domain.content.service;

import static com.example.udtbe.domain.content.entity.enums.StatAction.DECREASE;
import static com.example.udtbe.domain.content.entity.enums.StatAction.INCREASE;

import com.example.udtbe.domain.content.dto.FeedbackMapper;
import com.example.udtbe.domain.content.dto.common.FeedbackContentDTO;
import com.example.udtbe.domain.content.dto.common.FeedbackCreateDTO;
import com.example.udtbe.domain.content.dto.request.FeedbackContentGetRequest;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.entity.enums.StatAction;
import com.example.udtbe.domain.content.event.FeedbackStatEvent;
import com.example.udtbe.domain.content.repository.FeedbackRepository;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.dto.CursorPageResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackQuery feedbackQuery;
    private final FeedbackRepository feedbackRepository;
    private final ContentQuery contentQuery;
    private final FeedbackStatisticsQuery feedbackStatisticsQuery;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void saveFeedbacks(List<FeedbackCreateDTO> requests, Member member) {
        List<Feedback> feedbacks = new ArrayList<>();
        List<FeedbackStatEvent> statEvents = new ArrayList<>();

        for (FeedbackCreateDTO dto : requests) {
            Content content = feedbackQuery.findContentById(dto.contentId());
            List<GenreType> genres = contentQuery.getGenreTypeById(dto.contentId());
            FeedbackType newType = dto.feedback();

            feedbackQuery.findFeedbackByMemberIdAndContentId(
                    member.getId(), content.getId()).ifPresentOrElse(
                    prev -> handleExisting(prev, member, genres, newType, feedbacks, statEvents),
                    () -> handleCreate(member, content, genres, newType, feedbacks, statEvents)
            );
        }

        if (!feedbacks.isEmpty()) {
            feedbackRepository.saveAll(feedbacks);
        }
        statEvents.forEach(eventPublisher::publishEvent);
    }

    private void handleCreate(Member member,
            Content content,
            List<GenreType> genres,
            FeedbackType newType,
            List<Feedback> feedbacks,
            List<FeedbackStatEvent> statEvents) {

        feedbacks.add(Feedback.of(newType, false, member, content));
        addStatEvents(member, genres, newType, INCREASE, statEvents);
    }

    private void handleExisting(Feedback prev,
            Member member,
            List<GenreType> genres,
            FeedbackType newType,
            List<Feedback> feedbacks,
            List<FeedbackStatEvent> statEvents) {

        FeedbackType oldType = prev.getFeedbackType();

        if (prev.isDeleted()) {
            prev.switchDeleted();
            prev.updateFeedbackType(newType);
            addStatEvents(member, genres, newType, INCREASE, statEvents);
        } else if (!Objects.equals(oldType, newType)) {
            addStatEvents(member, genres, oldType, DECREASE, statEvents);
            addStatEvents(member, genres, newType, INCREASE, statEvents);
            prev.updateFeedbackType(newType);
        }

        feedbacks.add(prev);
    }


    private void addStatEvents(Member member, List<GenreType> genres, FeedbackType newType,
            StatAction statAction, List<FeedbackStatEvent> statEvents) {
        genres.stream()
                .map(g -> new FeedbackStatEvent(member, g, newType, statAction))
                .forEach(statEvents::add);
    }


    @Transactional(readOnly = true)
    public CursorPageResponse<FeedbackContentDTO> getFeedbackList(FeedbackContentGetRequest request,
            Member member) {
        List<Feedback> feedbacks = feedbackQuery.getFeedbacksByCursor(member, request);
        if (feedbacks == null) {
            feedbacks = Collections.emptyList();
        }

        boolean hasNext = feedbacks.size() > request.size();
        List<Feedback> limited = hasNext ? feedbacks.subList(0, request.size()) : feedbacks;

        List<FeedbackContentDTO> dtoList = FeedbackMapper.toResponseList(limited);

        Long nextCursorRaw = limited.isEmpty() ? null : limited.get(limited.size() - 1).getId();
        String nextCursor = nextCursorRaw == null ? null : nextCursorRaw.toString();

        return new CursorPageResponse<>(dtoList, nextCursor, hasNext);
    }

    @Transactional
    public void deleteFeedback(List<Long> feedbackIds, Member member) {
        List<Feedback> feedbacks = feedbackQuery.findFeedbackByIdList(member.getId(), feedbackIds);
        for (Feedback feedback : feedbacks) {
            feedback.switchDeleted();
            List<GenreType> genres = contentQuery.getGenreTypeById(feedback.getContent().getId());
            genres.forEach(
                    genreType -> feedbackStatisticsQuery.decreaseStatics(member, genreType,
                            feedback.getFeedbackType()));
        }
    }
}
