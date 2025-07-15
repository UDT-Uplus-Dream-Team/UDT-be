package com.example.udtbe.content.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.udtbe.common.support.DataJpaSupport;
import com.example.udtbe.domain.content.exception.ContentErrorCode;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.global.exception.RestApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ContentRepositoryTest extends DataJpaSupport {

    @Autowired
    ContentRepository contentRepository;

    @DisplayName("삭제된 콘텐츠를 조회할 수 없다.")
    @Test
    void throwExceptionWhenContentIsNotExist() {
        // when  // then
        assertThatThrownBy(() -> contentRepository.getContentDetails(1L))
                .isInstanceOf(RestApiException.class)
                .hasMessage(ContentErrorCode.CONTENT_NOT_FOUND.getMessage());
    }
}
