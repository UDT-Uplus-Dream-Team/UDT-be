package com.example.udtbe.batch.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.udtbe.common.fixture.AdminContentUpdateJobFixture;
import com.example.udtbe.common.support.DataJpaSupport;
import com.example.udtbe.domain.batch.entity.AdminContentUpdateJob;
import com.example.udtbe.domain.batch.repository.AdminContentUpdateJobRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AdminContentUpdateJobRepositoryTest extends DataJpaSupport {

    @Autowired
    private AdminContentUpdateJobRepository adminContentUpdateJobRepository;

    @DisplayName("AdminContentUpdateJob을 저장하고 조회할 수 있다.")
    @Test
    void updateJobSaveAndFind() {
        // given
        AdminContentUpdateJob job = AdminContentUpdateJobFixture.createPendingJob(1L, 1L, "타이틀",
                "설명");

        // when
        AdminContentUpdateJob savedJob = adminContentUpdateJobRepository.save(job);
        AdminContentUpdateJob foundJob = adminContentUpdateJobRepository.findById(savedJob.getId())
                .get();
        // then
        assertThat(foundJob.getId()).isEqualTo(savedJob.getId());
        assertThat(foundJob.getTitle()).isEqualTo(savedJob.getTitle());
    }

}
