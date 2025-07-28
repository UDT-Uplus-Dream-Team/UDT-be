package com.example.udtbe.domain.content.util;

import com.example.udtbe.domain.content.dto.common.ContentRecommendationDTO;
import com.example.udtbe.domain.content.event.IndexRebuildCompleteEvent;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RecommendationCacheManager {

    private final ConcurrentHashMap<Long, MemberRecommendationCache> memberCaches = new ConcurrentHashMap<>();
    private static final int EXPIRATION_HOURS = 12;

    public MemberRecommendationCache getCache(Long memberId) {
        MemberRecommendationCache cache = memberCaches.get(memberId);

        if (cache != null && cache.isExpired(EXPIRATION_HOURS)) {
            log.info("캐시 만료로 제거: memberId={}", memberId);
            memberCaches.remove(memberId);
            return null;
        }

        return cache;
    }

    public void putCache(Long memberId, List<ContentRecommendationDTO> recommendations) {
        MemberRecommendationCache cache = new MemberRecommendationCache(recommendations);
        memberCaches.put(memberId, cache);

        log.info("기본 추천 캐시 저장 완료: memberId={}, 추천 수={}", memberId, recommendations.size());
    }

    public boolean hasCache(Long memberId) {
        MemberRecommendationCache cache = memberCaches.get(memberId);
        return cache != null && !cache.isExpired(EXPIRATION_HOURS);
    }

    public void removeCache(Long memberId) {
        MemberRecommendationCache removed = memberCaches.remove(memberId);
        if (removed != null) {
            log.info("기본 추천 캐시 제거 완료: memberId={}", memberId);
        }
    }

    public void removeMemberCaches(Long memberId) {
        removeCache(memberId);
        log.info("사용자 캐시 제거 완료: memberId={}", memberId);
    }

    public void clearAllCaches() {
        int beforeSize = memberCaches.size();
        memberCaches.clear();
        log.info("전체 추천 캐시 클리어 완료: {}개 제거", beforeSize);
    }

    public int getCacheSize() {
        return memberCaches.size();
    }

    public void cleanExpiredCaches() {
        memberCaches.entrySet().removeIf(entry -> {
            boolean expired = entry.getValue().isExpired(EXPIRATION_HOURS);
            if (expired) {
                log.debug("만료된 캐시 정리: key={}", entry.getKey());
            }
            return expired;
        });
    }


    @EventListener
    public void handleIndexRebuildComplete(IndexRebuildCompleteEvent event) {
        log.info("인덱스 리빌드 완료 이벤트 수신: 인덱싱={}개, 시간={}ms",
                event.getIndexedCount(), event.getBuildTimeMs());

        clearAllCaches();
        log.info("인덱스 리빌드로 인한 추천 캐시 전체 무효화 완료");
    }
}