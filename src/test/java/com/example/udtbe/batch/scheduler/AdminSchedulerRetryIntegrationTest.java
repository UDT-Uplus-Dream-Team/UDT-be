package com.example.udtbe.batch.scheduler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.udtbe.domain.batch.scheduler.AdminScheduler;
import com.example.udtbe.domain.content.service.LuceneIndexService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.batch.job.enabled=false"  // 배치 자동 실행 비활성화
})
class AdminSchedulerRetryIntegrationTest {

    @Autowired
    private AdminScheduler adminScheduler;

    @MockBean
    private LuceneIndexService luceneIndexService;

    @Test
    @DisplayName("루씬 인덱스 리빌드 재시도 로직이 실제로 작동한다")
    void rebuildLuceneIndexWithRetry_ActualRetryTest() {
        // given
        doThrow(new RuntimeException("첫 번째 실패"))
                .doThrow(new RuntimeException("두 번째 실패"))
                .doThrow(new RuntimeException("세 번째 실패"))
                .doNothing()
                .when(luceneIndexService).buildIndexOnStartup();

        // when & then
        assertDoesNotThrow(() -> adminScheduler.rebuildLuceneIndexWithRetry());

        // maxAttempts=3에서 4번 호출됨 (첫 시도 + 재시도 3번)
        verify(luceneIndexService, times(4)).buildIndexOnStartup();
    }

    @Test
    @DisplayName("루씬 인덱스 리빌드 모든 재시도 실패 시 최종적으로 예외가 발생한다")
    void rebuildLuceneIndexWithRetry_AllRetriesFailTest() {
        // given
        RuntimeException exception = new RuntimeException("모든 시도 실패");
        doThrow(exception).when(luceneIndexService).buildIndexOnStartup();

        // when & then
        try {
            adminScheduler.rebuildLuceneIndexWithRetry();
        } catch (RuntimeException e) {
            // 예상된 예외
        }

        // maxAttempts=3에서 실제로는 3번 시도됨 (모든 재시도 실패)
        verify(luceneIndexService, times(3)).buildIndexOnStartup();
    }

    @Test
    @DisplayName("루씬 인덱스 리빌드가 첫 번째 시도에서 성공한다")
    void rebuildLuceneIndexWithRetry_FirstTrySuccessTest() {
        // given
        doNothing().when(luceneIndexService).buildIndexOnStartup();

        // when & then
        assertDoesNotThrow(() -> adminScheduler.rebuildLuceneIndexWithRetry());

        // 1번만 호출되었는지 확인
        verify(luceneIndexService, times(1)).buildIndexOnStartup();
    }
}