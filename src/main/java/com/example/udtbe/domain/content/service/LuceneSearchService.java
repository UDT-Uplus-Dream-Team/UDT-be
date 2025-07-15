package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.entity.enums.GenreType;
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
        
        DirectoryReader reader = luceneIndexService.getIndexReader();
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = luceneIndexService.getAnalyzer();

        BooleanQuery query = buildRecommendationQuery(platformFilteredContentIds, userGenres, analyzer);

        TopDocs topDocs = searcher.search(query, limit * 3);

        reader.close();
        return topDocs;
    }

    private BooleanQuery buildRecommendationQuery(List<Long> platformFilteredContentIds, 
            List<String> userGenres, Analyzer analyzer) throws ParseException {
        
        BooleanQuery.Builder mainQueryBuilder = new BooleanQuery.Builder();

        BooleanQuery.Builder idFilterBuilder = new BooleanQuery.Builder();
        for (Long contentId : platformFilteredContentIds) {
            idFilterBuilder.add(new TermQuery(new Term("contentId", contentId.toString())),
                    BooleanClause.Occur.SHOULD);
        }
        mainQueryBuilder.add(idFilterBuilder.build(), BooleanClause.Occur.MUST);

        int genreQueryCount = 0;
        if (userGenres != null && !userGenres.isEmpty()) {
            List<String> koreanUserGenres = GenreType.toKoreanTypes(userGenres);

            for (String koreanUserGenre : koreanUserGenres) {
                if (koreanUserGenre != null && !koreanUserGenre.trim().isEmpty()) {
                    QueryParser genreParser = new QueryParser("genreTag", analyzer);
                    Query genreQuery = genreParser.parse(koreanUserGenre);
                    mainQueryBuilder.add(genreQuery, BooleanClause.Occur.SHOULD);
                    genreQueryCount++;
                }
            }
        }

        return mainQueryBuilder.build();
    }
}