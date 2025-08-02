package com.example.udtbe.content.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.example.udtbe.common.fixture.DirectorFixture;
import com.example.udtbe.common.support.DataJpaSupport;
import com.example.udtbe.domain.admin.dto.request.AdminDirectorsGetRequest;
import com.example.udtbe.domain.admin.dto.response.AdminDirectorsGetResponse;
import com.example.udtbe.domain.content.entity.Director;
import com.example.udtbe.domain.content.repository.DirectorRepository;
import com.example.udtbe.global.dto.CursorPageResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DirectorRepositoryTest extends DataJpaSupport {

    @Autowired
    DirectorRepository directorRepository;

    @DisplayName("이름으로 감독을 검색한다.")
    @Test
    void getDirectorsThatContainsName() {
        // given
        Director director1 = DirectorFixture.director("김거북이");
        Director director2 = DirectorFixture.director("김두루미");
        Director director3 = DirectorFixture.director("김거미");

        directorRepository.saveAll(List.of(director1, director2, director3));

        AdminDirectorsGetRequest request = new AdminDirectorsGetRequest(
                director1.getDirectorName().substring(1, 2), null, 10
        );

        // when
        CursorPageResponse<AdminDirectorsGetResponse> response =
                directorRepository.getDirectors(request);

        // then
        assertAll(
                () -> assertThat(response.item()).hasSize(2),
                () -> assertThat(response.item().get(0).name()).isEqualTo(
                        director3.getDirectorName()
                ),
                () -> assertThat(response.item().get(1).name()).isEqualTo(
                        director1.getDirectorName()
                ),
                () -> assertThat(response.nextCursor()).isNull(),
                () -> assertFalse(response.hasNext())
        );
    }
}
