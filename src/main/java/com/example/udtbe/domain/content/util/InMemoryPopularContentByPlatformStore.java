package com.example.udtbe.domain.content.util;

import com.example.udtbe.domain.content.dto.response.PopularContentByPlatformResponse;
import com.example.udtbe.domain.content.service.ContentQuery;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryPopularContentByPlatformStore implements PopularContentByPlatformStore {

    private volatile CopyOnWriteArrayList<PopularContentByPlatformResponse> cache = new CopyOnWriteArrayList<>();
    private final ContentQuery contentQuery;

    @Override
    public List<PopularContentByPlatformResponse> get() {
        if (isEmpty()) {
            update();
        }
        return Collections.unmodifiableList(cache);
    }

    @Override
    public void update() {
        cache = initCache();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    @Transactional(readOnly = true)
    public CopyOnWriteArrayList<PopularContentByPlatformResponse> initCache() {
        List<PopularContentByPlatformResponse> responses = contentQuery.findPopularContentsByPlatform();
        return new CopyOnWriteArrayList<>(responses);
    }
}