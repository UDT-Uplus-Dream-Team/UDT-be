package com.example.udtbe.domain.content.util;

import com.example.udtbe.domain.content.dto.FeedbackMapper;
import com.example.udtbe.domain.content.dto.response.PopularContentsResponse;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.repository.FeedbackRepository;
import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InMemoryPopularContentStore implements PopularContentStore {

    private volatile CopyOnWriteArrayList<PopularContentsResponse> cache = new CopyOnWriteArrayList<>();

    private final FeedbackRepository feedbackRepository;

    @Override
    public List<PopularContentsResponse> get() {
        if (isEmpty()) {
            update();
        }

        return Collections.unmodifiableList(cache);
    }

    @Override
    @Scheduled(cron = "0 0 */12 * * *")
    public void update() {
        cache = initCache();
    }

    @PostConstruct
    protected void init() {
        update();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    @Transactional(readOnly = true)
    public CopyOnWriteArrayList<PopularContentsResponse> initCache() {
        List<PopularContentsResponse> popularContentsResponses = buildPopularContentReponses();
        return new CopyOnWriteArrayList<>(popularContentsResponses);
    }

    private List<PopularContentsResponse> buildPopularContentReponses() {
        List<Content> topRankedContents = feedbackRepository.findTopRankedContents(
                PageRequest.of(0, 10));

        return FeedbackMapper.toPopularContentsResponses(topRankedContents);
    }
}
