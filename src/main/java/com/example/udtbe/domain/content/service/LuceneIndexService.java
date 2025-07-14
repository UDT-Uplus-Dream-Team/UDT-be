package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.repository.ContentMetadataRepository;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LuceneIndexService {

    private final ContentMetadataRepository contentMetadataRepository;
    private final Analyzer analyzer;
    private final Directory directory;

    private volatile boolean indexBuilt = false;

    @EventListener(ApplicationReadyEvent.class)
    public void buildIndexOnStartup() {
        log.info("===== Lucene 인덱스 초기화 시작 =====");
        long startTime = System.currentTimeMillis();

        try {
            buildIndex();
            long endTime = System.currentTimeMillis();
            log.info("===== Lucene 인덱스 빌드 완료 =====");
            log.info("인덱스 빌드 시간: {}ms, 상태: {}", endTime - startTime, indexBuilt);
        } catch (Exception e) {
            log.error("Lucene 인덱스 빌드 실패: {}", e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public synchronized void buildIndex() throws IOException {
        log.debug("Lucene 인덱스 빌드 세부 작업 시작");
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        log.debug("IndexWriter 설정 생성 완료: analyzer={}", analyzer.getClass().getSimpleName());

        try (IndexWriter indexWriter = new IndexWriter(directory, config)) {
            indexWriter.deleteAll();

            log.debug("ContentMetadata 조회 시작");
            List<ContentMetadata> allContentMetadata = contentMetadataRepository.findByIsDeletedFalse();
            log.info("인덱싱 대상 ContentMetadata: {}개", allContentMetadata.size());

            int indexedCount = 0;
            for (ContentMetadata metadata : allContentMetadata) {
                try {
                    Document doc = createDocument(metadata);
                    indexWriter.addDocument(doc);
                    indexedCount++;

                    if (indexedCount % 100 == 0) {
                        log.debug("인덱싱 진행률: {}/{}", indexedCount, allContentMetadata.size());
                    }

                } catch (Exception e) {
                    log.warn("문서 인덱싱 실패 - contentId={}: {}",
                            metadata.getContent().getId(), e.getMessage());
                }
            }

            indexWriter.commit();
            indexBuilt = true;
            log.info("Lucene 인덱스 빌드 성공: {}개 문서 인덱싱 완료", indexedCount);
        }
    }

    private Document createDocument(ContentMetadata metadata) {
        Document doc = new Document();
        Long contentId = metadata.getContent().getId();

        doc.add(new LongPoint("contentId", contentId));
        doc.add(new StringField("contentId", contentId.toString(), Field.Store.YES));
        doc.add(new TextField("title", metadata.getTitle(), Field.Store.YES));

        // List<String> 타입의 태그들을 쉼표로 구분된 문자열로 변환
        String platformTag = metadata.getPlatformTag() != null ?
                String.join(",", metadata.getPlatformTag()) : "";
        String genreTag = metadata.getGenreTag() != null ?
                String.join(",", metadata.getGenreTag()) : "";
        String directorTag = metadata.getDirectorTag() != null ?
                String.join(",", metadata.getDirectorTag()) : "";
        String rating = metadata.getRating() != null ? metadata.getRating() : "";

        doc.add(new TextField("platformTag", platformTag, Field.Store.YES));
        doc.add(new TextField("genreTag", genreTag, Field.Store.YES));
//        doc.add(new TextField("directorTag", directorTag, Field.Store.YES));
//        doc.add(new StringField("rating", rating, Field.Store.YES));

        log.trace("문서 생성: contentId={}, title='{}', platforms='{}', genres='{}', rating='{}'",
                contentId, metadata.getTitle(), platformTag, genreTag, rating);

        return doc;
    }

    public DirectoryReader getIndexReader() throws IOException {
        if (!indexBuilt) {
            log.warn("인덱스가 빌드되지 않음 - 즉시 빌드 시작");
            buildIndex();
        }
        log.debug("DirectoryReader 생성: indexBuilt={}", indexBuilt);
        return DirectoryReader.open(directory);
    }

    public Analyzer getAnalyzer() {
        log.trace("Analyzer 반환: {}", analyzer.getClass().getSimpleName());
        return analyzer;
    }

    public boolean isIndexBuilt() {
        log.trace("인덱스 빌드 상태 확인: {}", indexBuilt);
        return indexBuilt;
    }
}