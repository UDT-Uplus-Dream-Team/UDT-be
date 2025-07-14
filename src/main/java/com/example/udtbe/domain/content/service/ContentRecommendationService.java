package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.dto.ContentRecommendation;
import com.example.udtbe.domain.content.dto.ContentRecommendationResponse;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.survey.entity.Survey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentRecommendationService {

    private final ContentRecommendationQuery contentRecommendationQuery;

    private final LuceneIndexService luceneIndexService;

    @Transactional(readOnly = true)
    public List<ContentRecommendationResponse> recommendContents(Member member, int limit) {
        log.info("===== 추천 시스템 시작 =====");
        log.info("요청 사용자: memberId={}, limit={}", member.getId(), limit);

        try {
            Optional<Survey> userSurvey = contentRecommendationQuery.findSurveyByMemberId(
                    member.getId());
            if (userSurvey.isEmpty()) {
                log.warn("사용자 설문 데이터 없음 - 인기 콘텐츠 반환: memberId={}", member.getId());
                return getPopularContents(limit);
            }

            log.info("사용자 설문 발견: platformTags={}, genreTags={}",
                    userSurvey.get().getPlatformTag(), userSurvey.get().getGenreTag());

            return searchRecommendations(userSurvey.get(), member, limit);

        } catch (Exception e) {
            log.error("추천 시스템 오류 발생: memberId={}, error={}", member.getId(), e.getMessage(), e);
            return getPopularContents(limit);
        }
    }

    public List<ContentRecommendationResponse> searchRecommendations(Survey userSurvey,
            Member member, int limit)
            throws Exception {
        log.info("--- 개인화 추천 검색 시작 ---");
        long startTime = System.currentTimeMillis();

        // 1. 모든 ContentMetadata를 한 번에 조회하여 캐시 생성 , 추후 메모리 분석 및 성능 개선의 여지가 농후
        Map<Long, ContentMetadata> metadataCache = contentRecommendationQuery.findContentMetadataCache();
        log.info("ContentMetadata 캐시 생성 완료: 총 {}개 콘텐츠", metadataCache.size());

        // 2. 캐시를 활용하여 플랫폼 필터링
        log.debug("플랫폼 필터링 시작: platforms={}", userSurvey.getPlatformTag());
        List<Long> platformFilteredContentIds = getPlatformFilteredContentIds(
                userSurvey.getPlatformTag(), metadataCache);
        log.info("플랫폼 필터링 완료: {}개 → {}개 콘텐츠",
                metadataCache.size(), platformFilteredContentIds.size());

        log.debug("Lucene 인덱스 리더 및 검색기 초기화");
        DirectoryReader reader = luceneIndexService.getIndexReader();
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = luceneIndexService.getAnalyzer();

        // 플랫폼 필터링된 컨텐츠 ID들로 제한하는 쿼리
        log.debug("Lucene 쿼리 빌드 시작");
        BooleanQuery.Builder mainQueryBuilder = new BooleanQuery.Builder();

        // 플랫폼 필터링된 ID들을 OR 조건으로 연결
        BooleanQuery.Builder idFilterBuilder = new BooleanQuery.Builder();
        for (Long contentId : platformFilteredContentIds) {
            idFilterBuilder.add(new TermQuery(new Term("contentId", contentId.toString())),
                    BooleanClause.Occur.SHOULD);
        }
        mainQueryBuilder.add(idFilterBuilder.build(), BooleanClause.Occur.MUST);
        log.debug("플랫폼 필터 쿼리 추가: {}개 ID", platformFilteredContentIds.size());

        // 장르 유사도 쿼리 추가 (List<String>으로 변경)
        List<String> userGenres = userSurvey.getGenreTag();
        int genreQueryCount = 0;
        if (userGenres != null && !userGenres.isEmpty()) {
            for (String userGenre : userGenres) {
                if (userGenre != null && !userGenre.trim().isEmpty()) {
                    QueryParser genreParser = new QueryParser("genreTag", analyzer);
                    Query genreQuery = genreParser.parse(userGenre);
                    mainQueryBuilder.add(genreQuery, BooleanClause.Occur.SHOULD);
                    genreQueryCount++;
                }
            }
        }
        log.debug("장르 유사도 쿼리 추가: {}개 장르", genreQueryCount);

        BooleanQuery query = mainQueryBuilder.build();
        log.debug("최종 Lucene 쿼리: {}", query.toString());

        TopDocs topDocs = searcher.search(query, limit * 3);
        log.info("Lucene 검색 완료: {}개 결과 (요청 {}개)", topDocs.scoreDocs.length, limit * 3);

        List<ContentRecommendation> recommendations = new ArrayList<>();

        // 3. 캐시를 활용하여 피드백 점수 계산
        log.debug("사용자 피드백 점수 계산 시작");
        Map<String, Float> feedbackScores = calculateGenreFeedbackScores(member, metadataCache);
        log.info("피드백 점수 계산 완료: {}개 장르에 대한 점수", feedbackScores.size());
        log.debug("장르별 피드백 점수: {}", feedbackScores);

        log.debug("점수 계산 및 추천 목록 생성 시작");
        for (int i = 0; i < topDocs.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = topDocs.scoreDocs[i];
            Document doc = searcher.storedFields().document(scoreDoc.doc);
            Long contentId = Long.valueOf(doc.get("contentId"));
            // TF-IDF 점수
            float luceneScore = scoreDoc.score;
            //설문 기반 선호도
            float genreBoost = calculateGenreBoost(doc, userGenres);
            //실제 행동 기반 선호도
            float feedbackScore = calculateGenreFeedbackBoost(doc, feedbackScores);

            float finalScore = luceneScore + genreBoost * 2.0f + feedbackScore;

            log.debug("콘텐츠 점수 계산 [{}]: contentId={}, lucene={}, genre={}, feedback={}, final={}",
                    i + 1, contentId, String.format("%.3f", luceneScore),
                    String.format("%.3f", genreBoost), String.format("%.3f", feedbackScore),
                    String.format("%.3f", finalScore));

            recommendations.add(new ContentRecommendation(contentId, finalScore));
        }
        log.info("점수 계산 완료: {}개 콘텐츠 점수 계산", recommendations.size());

        reader.close();

        log.debug("최종 추천 목록 정렬 및 제한");
        List<ContentRecommendation> sortedRecommendations = recommendations.stream()
                .sorted((r1, r2) -> Float.compare(r2.score(), r1.score()))
                .limit(limit)
                .toList();

        // 상위 추천 콘텐츠 점수 로깅
        log.info("=== 최종 추천 결과 TOP {} ===", Math.min(limit, sortedRecommendations.size()));
        for (int i = 0; i < Math.min(5, sortedRecommendations.size()); i++) {
            ContentRecommendation rec = sortedRecommendations.get(i);
            log.info("순위 {}: contentId={}, 최종점수={}",
                    i + 1, rec.contentId(), String.format("%.3f", rec.score()));
        }

        List<Long> sortedContentIds = sortedRecommendations.stream()
                .map(ContentRecommendation::contentId)
                .toList();

        log.debug("Content 엔티티 조회: {}개 ID", sortedContentIds.size());
        List<Content> contents = contentRecommendationQuery.findContentsByIds(sortedContentIds);

        // 4. 캐시에서 해당하는 ContentMetadata 들을 추출
        log.debug("캐시에서 ContentMetadata 추출");
        List<ContentMetadata> metadataList = contents.stream()
                .map(content -> metadataCache.get(content.getId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info("--- 개인화 추천 검색 완료 ---");
        log.info("총 처리 시간: {}ms, 최종 반환: {}개 콘텐츠",
                endTime - startTime, contents.size());

        return ContentRecommendationResponse.of(contents, metadataList);
    }

    private List<Long> getPlatformFilteredContentIds(List<String> platformTags,
            Map<Long, ContentMetadata> metadataCache) {
        log.debug("플랫폼 필터링 상세 로직 시작");

        if (platformTags == null || platformTags.isEmpty()) {
            log.debug("플랫폼 태그 없음 - 모든 콘텐츠 반환");
            return new ArrayList<>(metadataCache.keySet());
        }

        // 여러 플랫폼 태그 중 하나라도 포함된 컨텐츠 필터링 (캐시 활용)
        Set<Long> contentIds = new HashSet<>();
        for (String platformTag : platformTags) {
            if (platformTag != null && !platformTag.trim().isEmpty()) {
                int matchCount = 0;
                for (Map.Entry<Long, ContentMetadata> entry : metadataCache.entrySet()) {
                    ContentMetadata metadata = entry.getValue();
                    if (metadata.getPlatformTag() != null &&
                            metadata.getPlatformTag().contains(platformTag)) {
                        contentIds.add(entry.getKey());
                        matchCount++;
                    }
                }
                log.debug("플랫폼 '{}' 매칭: {}개 콘텐츠", platformTag, matchCount);
            }
        }

        log.debug("플랫폼 필터링 결과: {}개 콘텐츠 선택", contentIds.size());
        return new ArrayList<>(contentIds);
    }

    private float calculateGenreBoost(Document doc, List<String> userGenres) {
        if (userGenres == null || userGenres.isEmpty()) {
            return 0.0f;
        }

        String genreTag = doc.get("genreTag");
        if (genreTag == null) {
            return 0.0f;
        }
        float boost = 0.0f;
        String[] docGenres = genreTag.split(",");
        for (String userGenre : userGenres) {
            if (userGenre != null && !userGenre.trim().isEmpty()) {
                for (String docGenre : docGenres) {
                    if (docGenre.trim().equals(userGenre.trim())) {
                        boost += 1.0f;
                        break; // 중복 매칭 방지
                    }
                }
            }
        }
        return boost;
    }

    private float calculateGenreFeedbackBoost(Document doc, Map<String, Float> genreScores) {
        String docGenreTag = doc.get("genreTag");
        if (docGenreTag == null) {
            return 0.0f;
        }

        float boost = 0.0f;
        String[] docGenres = docGenreTag.split(",");
        for (String genre : docGenres) {
            genre = genre.trim();
            boost += genreScores.getOrDefault(genre, 0.0f);
        }
        return boost;
    }

    //좋아하는 콘텐츠의 장르에 점수부여 (캐시 활용)
    private Map<String, Float> calculateGenreFeedbackScores(Member member,
            Map<Long, ContentMetadata> metadataCache) {
        log.debug("사용자 피드백 점수 계산 상세 로직 시작: memberId={}", member.getId());
        Map<String, Float> genreScores = new HashMap<>();

        List<Feedback> feedbacks = contentRecommendationQuery.findFeedbacksByMemberId(
                member.getId());
        if (feedbacks == null || feedbacks.isEmpty()) {
            log.debug("사용자 피드백 없음");
            return genreScores;
        }

        log.debug("사용자 피드백 발견: {}개", feedbacks.size());

        // 피드백 처리 (캐시 활용)
        int processedCount = 0;
        for (Feedback feedback : feedbacks) {
            if (!feedback.isDeleted()) {
                Long contentId = feedback.getContent().getId();
                ContentMetadata metadata = metadataCache.get(contentId);

                if (metadata != null && metadata.getGenreTag() != null) {
                    float score = switch (feedback.getFeedbackType()) {
                        case LIKE -> 1.0f;
                        case DISLIKE -> -1.0f;
                        case UNINTERESTED -> -0.5f;
                    };

                    log.debug("피드백 처리: contentId={}, type={}, score={}",
                            contentId, feedback.getFeedbackType(), score);

                    // genreTag는 이제 List<String> 타입이므로 직접 사용
                    for (String genre : metadata.getGenreTag()) {
                        if (genre != null && !genre.trim().isEmpty()) {
                            genre = genre.trim();
                            float oldScore = genreScores.getOrDefault(genre, 0.0f);
                            float newScore = oldScore + score;
                            genreScores.put(genre, newScore);
                            log.trace("장르 점수 업데이트: {} {} → {}",
                                    genre, String.format("%.2f", oldScore),
                                    String.format("%.2f", newScore));
                        }
                    }
                    processedCount++;
                } else {
                    log.trace("메타데이터 없음: contentId={}", contentId);
                }
            }
        }

        log.debug("피드백 점수 계산 완료: {}개 피드백 처리, {}개 장르 점수 생성",
                processedCount, genreScores.size());
        return genreScores;
    }

    private List<ContentRecommendationResponse> getPopularContents(int limit) {
        log.info("인기 콘텐츠 조회 시작: limit={}", limit);

        List<ContentMetadata> metadataList = contentRecommendationQuery.findPopularContentMetadata(
                limit);

        List<Content> contents = metadataList.stream()
                .map(ContentMetadata::getContent)
                .toList();

        log.info("인기 콘텐츠 조회 완료: {}개 반환", contents.size());
        return ContentRecommendationResponse.of(contents, metadataList);
    }
}