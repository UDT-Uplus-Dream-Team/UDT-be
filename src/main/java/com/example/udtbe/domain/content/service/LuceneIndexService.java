package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.event.IndexRebuildCompleteEvent;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LuceneIndexService {

    private final ContentMetadataRepository contentMetadataRepository;
    private final Analyzer analyzer;
    private final Directory directory;
    private final ApplicationEventPublisher eventPublisher;

    private boolean indexBuilt = false;

    @EventListener(ApplicationReadyEvent.class)
    public void buildIndexOnStartup() {
        log.info("===== Lucene 인덱스 초기화 시작 =====");
        long startTime = System.currentTimeMillis();

        try {
            int successCount = buildIndex();
            long endTime = System.currentTimeMillis();
            long buildTime = endTime - startTime;
            
            log.info("===== Lucene 인덱스 빌드 완료: {}ms =====", buildTime);
            
            // 인덱스 리빌드 완료 이벤트 발행
            eventPublisher.publishEvent(new IndexRebuildCompleteEvent(this, successCount, buildTime));
        } catch (Exception e) {
            log.error("Lucene 인덱스 빌드 실패", e);
        }
    }

    private int buildIndex() throws IOException {
        List<ContentMetadata> allContentMetadata = contentMetadataRepository.findByIsDeletedFalse();
        log.info("인덱싱 대상 ContentMetadata: {}개", allContentMetadata.size());

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try (IndexWriter indexWriter = new IndexWriter(directory, config)) {
            indexWriter.deleteAll();

            int successCount = 0;
            for (ContentMetadata metadata : allContentMetadata) {
                try {
                    Document doc = createDocument(metadata);
                    indexWriter.addDocument(doc);
                    successCount++;
                } catch (Exception e) {
                    log.warn("문서 인덱싱 실패 - contentId={}: {}",
                            metadata.getContent().getId(), e.getMessage());
                }
            }

            indexWriter.commit();
            indexBuilt = true;
            log.info("인덱싱 완료: {}/{}개 성공", successCount, allContentMetadata.size());
            
            return successCount;
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

        log.trace("문서 생성: contentId={}, title='{}', platforms='{}', genres='{}', rating='{}'",
                contentId, metadata.getTitle(), platformTag, genreTag, rating);

        return doc;
    }

    public DirectoryReader getIndexReader() throws IOException {
        if (!indexBuilt) {
            throw new IllegalStateException("인덱스가 아직 빌드되지 않았습니다");
        }
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