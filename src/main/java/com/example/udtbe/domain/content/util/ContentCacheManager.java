package com.example.udtbe.domain.content.util;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContentCacheManager {

    private final PopularContentStore popularContentStore;
    private final PopularContentByPlatformStore popularContentByPlatformStore;

    @Scheduled(cron = "0 0 */12 * * *")
    public void refreshAllCaches() {
        popularContentStore.update();
        popularContentByPlatformStore.update();
        log.info("콘텐츠 캐싱 성공");
    }

    @PostConstruct
    public void initAllCaches() {
        CompletableFuture.runAsync(() -> {
            try {
                refreshAllCaches();
            } catch (Exception e) {
                log.error("서버 시작시 큐레이션 콘텐츠 캐싱 실패", e);
            }
        });
    }
}