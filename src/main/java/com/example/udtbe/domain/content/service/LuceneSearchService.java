package com.example.udtbe.domain.content.service;

import static org.apache.lucene.search.BooleanQuery.Builder;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LuceneSearchService {

    private final LuceneIndexService luceneIndexService;

    public TopDocs searchRecommendations(List<Long> platformFilteredContentIds,
            List<String> userGenres, int limit) throws IOException, ParseException {

        try (DirectoryReader indexReader = luceneIndexService.getIndexReader()) {
            IndexSearcher searcher = new IndexSearcher(indexReader);
            Analyzer analyzer = luceneIndexService.getAnalyzer();

            BooleanQuery query = buildRecommendationQuery(platformFilteredContentIds, userGenres,
                    analyzer);

            return searcher.search(query, limit * 10);
        } catch (IOException e) {
            log.error("Lucene 검색 실행 중 파일 시스템 오류: platformIds={}, genres={}, error={}", 
                     platformFilteredContentIds.size(), userGenres, e.getMessage(), e);
            throw e;
        } catch (ParseException e) {
            log.warn("검색 쿼리 파싱 실패: genres={}, error={}", userGenres, e.getMessage(), e);
            throw e;
        }
    }

    public TopDocs searchCuratedRecommendations(List<Long> platformFilteredContentIds,
            List<String> feedbackGenres, int limit)
            throws IOException, ParseException {

        try (DirectoryReader indexReader = luceneIndexService.getIndexReader()) {
            IndexSearcher searcher = new IndexSearcher(indexReader);
            Analyzer analyzer = luceneIndexService.getAnalyzer();

            BooleanQuery query = buildCuratedRecommendationQuery(platformFilteredContentIds,
                    feedbackGenres, analyzer);

            return searcher.search(query, limit * 2);
        } catch (IOException e) {
            log.error("Lucene 엄선된 추천 검색 중 파일 시스템 오류: platformIds={}, feedbackGenres={}, error={}", 
                     platformFilteredContentIds.size(), feedbackGenres, e.getMessage(), e);
            throw e;
        } catch (ParseException e) {
            log.warn("엄선된 추천 쿼리 파싱 실패: feedbackGenres={}, error={}", feedbackGenres, e.getMessage(), e);
            throw e;
        }
    }

    private BooleanQuery buildCuratedRecommendationQuery(List<Long> platformFilteredContentIds,
            List<String> feedbackGenres, Analyzer analyzer)
            throws ParseException {
        Builder mainQueryBuilder = new Builder();

        Builder idFilterBuilder = new Builder();
        for (Long contentId : platformFilteredContentIds) {
            idFilterBuilder.add(new TermQuery(new Term("contentId", contentId.toString())),
                    BooleanClause.Occur.SHOULD);
        }
        mainQueryBuilder.add(idFilterBuilder.build(), BooleanClause.Occur.MUST);

        if (feedbackGenres != null && !feedbackGenres.isEmpty()) {
            for (String feedbackGenre : feedbackGenres) {
                if (feedbackGenre != null && !feedbackGenre.trim().isEmpty()) {
                    String escapedGenre = QueryParser.escape(feedbackGenre);
                    QueryParser genreParser = new QueryParser("genreTag", analyzer);
                    Query genreQuery = genreParser.parse(escapedGenre);
                    // 피드백 기반 장르에 더 높은 우선순위 부여
                    mainQueryBuilder.add(genreQuery, BooleanClause.Occur.SHOULD);
                    log.trace("피드백 기반 장르 추가: {}", feedbackGenre);
                }
            }
        }

        return mainQueryBuilder.build();

    }

    //구독중인 플랫폼의 컨텐츠들, 좋아하는 장르(우선순위)를 기준으로 쿼리를 생성
    private BooleanQuery buildRecommendationQuery(List<Long> platformFilteredContentIds,
            List<String> userGenres, Analyzer analyzer) throws ParseException {

        Builder mainQueryBuilder = new Builder();

        Builder idFilterBuilder = new Builder();
        for (Long contentId : platformFilteredContentIds) {
            idFilterBuilder.add(new TermQuery(new Term("contentId", contentId.toString())),
                    BooleanClause.Occur.SHOULD);
        }
        mainQueryBuilder.add(idFilterBuilder.build(), BooleanClause.Occur.MUST);

        if (userGenres != null && !userGenres.isEmpty()) {

            for (String userGenre : userGenres) {
                if (userGenre != null && !userGenre.trim().isEmpty()) {
                    String escapedGenre = QueryParser.escape(userGenre);
                    QueryParser genreParser = new QueryParser("genreTag", analyzer);
                    Query genreQuery = genreParser.parse(escapedGenre);
                    mainQueryBuilder.add(genreQuery, BooleanClause.Occur.SHOULD);
                }
            }
        }

        return mainQueryBuilder.build();
    }
}