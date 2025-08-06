package com.example.udtbe.batch.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.udtbe.common.fixture.BatchJobMetricFixture;
import com.example.udtbe.common.support.DataJpaSupport;
import com.example.udtbe.domain.batch.entity.BatchJobMetric;
import com.example.udtbe.domain.batch.entity.enums.BatchJobType;
import com.example.udtbe.domain.batch.repository.BatchJobMetricRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BatchJobMetricRepositoryTest extends DataJpaSupport {

    @Autowired
    private BatchJobMetricRepository batchJobMetricRepository;

    @DisplayName("배치 작업 메트릭을 저장하고 조회할 수 있다.")
    @Test
    void saveAndFindAll() {
        // given
        BatchJobMetric metric1 = BatchJobMetricFixture.completedJob(1L,
                BatchJobType.REGISTER, 100);
        BatchJobMetric metric2 = BatchJobMetricFixture.partialCompetedJob(2L,
                BatchJobType.UPDATE, 100, 40);

        batchJobMetricRepository.saveAll(List.of(metric1, metric2));

        // when
        List<BatchJobMetric> metrics = batchJobMetricRepository.findAll();

        // then
        assertThat(metrics).hasSize(2);
    }
}