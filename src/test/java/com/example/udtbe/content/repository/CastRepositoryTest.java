package com.example.udtbe.content.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.example.udtbe.common.fixture.CastFixture;
import com.example.udtbe.common.support.DataJpaSupport;
import com.example.udtbe.domain.admin.dto.request.AdminCastsGetRequest;
import com.example.udtbe.domain.admin.dto.response.AdminCastsGetResponse;
import com.example.udtbe.domain.content.entity.Cast;
import com.example.udtbe.domain.content.repository.CastRepository;
import com.example.udtbe.global.dto.CursorPageResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CastRepositoryTest extends DataJpaSupport {

    @Autowired
    CastRepository castRepository;

    @DisplayName("이름으로 출연진을 검색한다.")
    @Test
    void getCastsThatContainsName() {
        // given
        Cast cast1 = CastFixture.cast("김거북이");
        Cast cast2 = CastFixture.cast("김두루미");
        Cast cast3 = CastFixture.cast("김거미");

        castRepository.saveAll(List.of(cast1, cast2, cast3));

        AdminCastsGetRequest request = new AdminCastsGetRequest(
                cast1.getCastName().substring(1, 2), null, 10
        );

        // when
        CursorPageResponse<AdminCastsGetResponse> response = castRepository.getCasts(request);

        // then
        assertAll(
                () -> assertThat(response.item()).hasSize(2),
                () -> assertThat(response.item().get(0).name()).isEqualTo(cast3.getCastName()),
                () -> assertThat(response.item().get(1).name()).isEqualTo(cast1.getCastName()),
                () -> assertThat(response.nextCursor()).isNull(),
                () -> assertFalse(response.hasNext())
        );
    }
}
