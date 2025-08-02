package com.example.udtbe.batch.scheduler;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import com.example.udtbe.domain.batch.scheduler.AdminScheduler;
import com.example.udtbe.domain.content.service.LuceneIndexService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminSchedulerTest {

    @Mock
    private LuceneIndexService luceneIndexService;

    @InjectMocks
    private AdminScheduler adminScheduler;

    @Test
    @DisplayName("루씬 인덱스 리빌드 재시도 메서드가 정상 작동한다")
    void rebuildLuceneIndexWithRetry_Success() {
        // given
        doNothing().when(luceneIndexService).buildIndexOnStartup();

        // when
        adminScheduler.rebuildLuceneIndexWithRetry();

        // then
        verify(luceneIndexService).buildIndexOnStartup();
    }
}