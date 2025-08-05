package com.example.udtbe.batch.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.udtbe.common.fixture.AdminContentDeleteJobFixture;
import com.example.udtbe.common.support.DataJpaSupport;
import com.example.udtbe.domain.batch.entity.AdminContentDeleteJob;
import com.example.udtbe.domain.batch.repository.AdminContentDeleteJobRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AdminContentDeleteJobRepositoryTest extends DataJpaSupport {

    @Autowired
    private AdminContentDeleteJobRepository adminContentDeleteJobRepository;


    @DisplayName("AdminContentDeleteJob을 저장하고 조회할 수 있다.")
    @Test
    void deleteJobSaveAndFind() {
        // given
        AdminContentDeleteJob job = AdminContentDeleteJobFixture.createPendingJob(1L, 1L);
        // when
        AdminContentDeleteJob savedJob = adminContentDeleteJobRepository.save(job);
        AdminContentDeleteJob foundJob = adminContentDeleteJobRepository.findById(savedJob.getId())
                .get();
        // then
        assertThat(foundJob.getId()).isEqualTo(savedJob.getId());
        assertThat(foundJob.getContentId()).isEqualTo(savedJob.getContentId());
    }

}
