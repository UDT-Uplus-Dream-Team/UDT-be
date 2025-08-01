package com.example.udtbe.batch.config;

import static com.example.udtbe.domain.member.entity.enums.Role.ROLE_ADMIN;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.udtbe.common.fixture.AdminContentDeleteJobFixture;
import com.example.udtbe.common.fixture.AdminContentRegisterJobFixture;
import com.example.udtbe.common.fixture.AdminContentUpdateJobFixture;
import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.ContentMetadataFixture;
import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.common.support.ApiSupport;
import com.example.udtbe.domain.batch.entity.AdminContentDeleteJob;
import com.example.udtbe.domain.batch.entity.AdminContentRegisterJob;
import com.example.udtbe.domain.batch.entity.AdminContentUpdateJob;
import com.example.udtbe.domain.batch.entity.enums.BatchStatus;
import com.example.udtbe.domain.batch.repository.AdminContentDeleteJobRepository;
import com.example.udtbe.domain.batch.repository.AdminContentRegisterJobRepository;
import com.example.udtbe.domain.batch.repository.AdminContentUpdateJobRepository;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.repository.ContentMetadataRepository;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.repository.MemberRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;

@SpringBatchTest
@Sql(scripts = "classpath:data-test.sql")
class BatchConfigTest extends ApiSupport {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private AdminContentUpdateJobRepository adminContentUpdateJobRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ContentMetadataRepository contentMetadataRepository;
    @Autowired
    private AdminContentDeleteJobRepository adminContentDeleteJobRepository;
    @Autowired
    private AdminContentRegisterJobRepository adminContentRegisterJobRepository;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn,
                    new ClassPathResource("org/springframework/batch/core/schema-drop-mysql.sql"));
            ScriptUtils.executeSqlScript(conn,
                    new ClassPathResource("org/springframework/batch/core/schema-mysql.sql"));
        }
    }


    @AfterEach
    void tearDown() {
        adminContentUpdateJobRepository.deleteAllInBatch();
        adminContentDeleteJobRepository.deleteAllInBatch();
        adminContentRegisterJobRepository.deleteAllInBatch();
        contentRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("content Job들을 수행할 수 있다.")
    void contentJobSuccess() throws Exception {
        // given
        List<Content> updateContents = new ArrayList<>();
        List<ContentMetadata> updateContentMetadatas = new ArrayList<>();
        List<Content> deleteContents = new ArrayList<>();
        List<ContentMetadata> deleteContentMetadatas = new ArrayList<>();

        List<AdminContentUpdateJob> updateJobs = new ArrayList<>();
        List<AdminContentDeleteJob> deleteJobs = new ArrayList<>();
        List<AdminContentRegisterJob> registerJobs = new ArrayList<>();

        Member member = MemberFixture.member("String@naber.com", ROLE_ADMIN);
        memberRepository.save(member);

        Content updateContent1 = ContentFixture.content("tit;e", "dis");
        updateContentMetadatas.add(ContentMetadataFixture.dramaMetadata(updateContent1));
        updateContents.add(updateContent1);

        contentRepository.saveAll(updateContents);
        contentMetadataRepository.saveAll(updateContentMetadatas);

        Content delContent1 = ContentFixture.content("del", "delete");
        Content delContent2 = ContentFixture.content("del", "delete");

        ContentMetadata delContentMetadata1 = ContentMetadataFixture.dramaMetadata(delContent1);
        ContentMetadata delContentMetadata2 = ContentMetadataFixture.dramaMetadata(delContent2);

        deleteContents.addAll(List.of(delContent1, delContent2));
        deleteContentMetadatas.addAll(List.of(delContentMetadata1, delContentMetadata2));

        contentRepository.saveAll(deleteContents);
        contentMetadataRepository.saveAll(deleteContentMetadatas);

        updateJobs.add(AdminContentUpdateJobFixture.createPendingJob(member.getId(),
                updateContent1.getId(), "title_job1", "설명1"));
        adminContentUpdateJobRepository.saveAll(updateJobs);

        deleteJobs.add(
                AdminContentDeleteJobFixture.createPendingJob(member.getId(), delContent1.getId()));
        deleteJobs.add(
                AdminContentDeleteJobFixture.createPendingJob(member.getId(), delContent2.getId()));
        adminContentDeleteJobRepository.saveAll(deleteJobs);

        registerJobs.add(AdminContentRegisterJobFixture.createPendingJob(
                member.getId(), "추가 콘텐츠1", "타이틀"));
        registerJobs.add(AdminContentRegisterJobFixture.createPendingJob(
                member.getId(), "추가 콘텐츠2", "타이틀"));
        adminContentRegisterJobRepository.saveAll(registerJobs);

        JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        // update 검증
        List<AdminContentUpdateJob> findUpdateJobs = adminContentUpdateJobRepository.findAll();
        assertThat(findUpdateJobs.get(0).getStatus().name()).isEqualTo(
                BatchStatus.COMPLETED.name());

        Content updatedContent = contentRepository.findById(updateContents.get(0).getId()).get();
        assertThat(updatedContent.getTitle()).isEqualTo(findUpdateJobs.get(0).getTitle());

        // delete 검증
        List<AdminContentDeleteJob> findDeleteJobs = adminContentDeleteJobRepository.findAll();
        assertThat(findDeleteJobs.get(0).getStatus().name()).isEqualTo(
                BatchStatus.COMPLETED.name());
        assertThat(findDeleteJobs.get(1).getStatus().name()).isEqualTo(
                BatchStatus.COMPLETED.name());

        Content deleteContent1 = contentRepository.findById(deleteContents.get(0).getId()).get();
        assertThat(deleteContent1.isDeleted()).isEqualTo(true);

        Content deleteContent2 = contentRepository.findById(deleteContents.get(1).getId()).get();
        assertThat(deleteContent2.isDeleted()).isEqualTo(true);

        ContentMetadata deleteContentMetadata1 = contentMetadataRepository.findById(
                deleteContentMetadatas.get(0).getId()).get();
        assertThat(deleteContentMetadata1.isDeleted()).isEqualTo(true);

        ContentMetadata deleteContentMetadata2 = contentMetadataRepository.findById(
                deleteContentMetadatas.get(1).getId()).get();
        assertThat(deleteContentMetadata2.isDeleted()).isEqualTo(true);

        List<AdminContentRegisterJob> findRegisterJobs = adminContentRegisterJobRepository.findAll();
        assertThat(findRegisterJobs.get(0).getStatus().name()).isEqualTo(
                BatchStatus.COMPLETED.name());
        assertThat(findRegisterJobs.get(1).getStatus().name()).isEqualTo(
                BatchStatus.COMPLETED.name());
    }
}