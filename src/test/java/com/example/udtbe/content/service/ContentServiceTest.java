package com.example.udtbe.content.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.CuratedContentFixture;
import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.CuratedContent;
import com.example.udtbe.domain.content.exception.ContentErrorCode;
import com.example.udtbe.domain.content.service.ContentQuery;
import com.example.udtbe.domain.content.service.ContentService;
import com.example.udtbe.domain.content.util.PopularContentStore;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.entity.enums.Role;
import com.example.udtbe.global.config.WeeklyGenrePolicyProperties;
import com.example.udtbe.global.exception.RestApiException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    private final static String email = "test@naver.com";

    @Mock
    private ContentQuery contentQuery;

    @Mock
    private WeeklyGenrePolicyProperties weeklyGenrePolicyProperties;

    @Mock
    private PopularContentStore popularContentStore;

    @InjectMocks
    private ContentService contentService;

    @DisplayName("새로운 엄선된 콘텐츠를 저장할 수 있다.")
    @Test
    void saveCuratedContent_NewContent_Success() {
        // Given
        Long contentId = 1L;
        Member member = MemberFixture.member(email, Role.ROLE_USER);
        Content content = ContentFixture.content("해리포터", "빅잼");

        given(contentQuery.findCuratedContentByMemberIdAndContentId(member.getId(), contentId))
                .willReturn(Optional.empty());
        given(contentQuery.getReferenceById(contentId))
                .willReturn(content);

        // When
        contentService.saveCuratedContent(contentId, member);

        // Then
        verify(contentQuery).findCuratedContentByMemberIdAndContentId(member.getId(), contentId);
        verify(contentQuery).getReferenceById(contentId);
        verify(contentQuery).saveCuratedContent(any(CuratedContent.class));
    }

    @DisplayName("이미 저장된 엄선된 콘텐츠를 다시 저장하려고 하면 예외가 발생한다.")
    @Test
    void saveCuratedContent_AlreadyActiveContent_ThrowsException() {
        // Given
        Long contentId = 1L;
        Member member = MemberFixture.member(email, Role.ROLE_USER);
        Content content = ContentFixture.content("해리포터", "노잼");
        CuratedContent activeCuratedContent = CuratedContentFixture.curatedContent(member, content);

        given(contentQuery.findCuratedContentByMemberIdAndContentId(member.getId(), contentId))
                .willReturn(Optional.of(activeCuratedContent));

        // When & Then
        assertThatThrownBy(() -> contentService.saveCuratedContent(contentId, member))
                .isInstanceOf(RestApiException.class)
                .hasMessage(ContentErrorCode.ALREADY_CURATED_CONTENT.getMessage());

        verify(contentQuery).findCuratedContentByMemberIdAndContentId(member.getId(), contentId);
        verify(contentQuery, never()).getReferenceById(contentId);
        verify(contentQuery, never()).saveCuratedContent(any(CuratedContent.class));
    }

    @DisplayName("삭제된 엄선된 콘텐츠를 다시 저장하면 재활성화된다.")
    @Test
    void saveCuratedContent_DeletedContent_Reactivates() {
        // Given
        Long contentId = 1L;
        Member member = MemberFixture.member(email, Role.ROLE_USER);
        Content content = ContentFixture.content("해뤼포뤌", "햄부기햄북쓰딱쓰");
        CuratedContent deletedCuratedContent = CuratedContentFixture.deletedCuratedContent(member,
                content);

        given(contentQuery.findCuratedContentByMemberIdAndContentId(member.getId(), contentId))
                .willReturn(Optional.of(deletedCuratedContent));

        // When
        contentService.saveCuratedContent(contentId, member);

        // Then
        verify(contentQuery).findCuratedContentByMemberIdAndContentId(member.getId(), contentId);
        verify(contentQuery, never()).getReferenceById(contentId);
        verify(contentQuery, never()).saveCuratedContent(any(CuratedContent.class));
    }

    @DisplayName("엄선된 콘텐츠를 삭제할 수 있다.")
    @Test
    void deleteCuratedContent_Success() {
        // Given
        Long contentId = 1L;
        Member member = MemberFixture.member(email, Role.ROLE_USER);
        Content content = ContentFixture.content("해리포터", "삭제될예정");
        CuratedContent activeCuratedContent = CuratedContentFixture.activeCuratedContent(member,
                content);

        given(contentQuery.findCuratedContentByMemberIdAndContentId(member.getId(), contentId))
                .willReturn(Optional.of(activeCuratedContent));

        // When
        contentService.deleteCuratedContent(member.getId(), contentId);

        // Then
        verify(contentQuery).findCuratedContentByMemberIdAndContentId(member.getId(), contentId);
        assertThat(activeCuratedContent.isDeleted()).isTrue();
    }

    @DisplayName("이미 삭제된 콘텐츠를 다시 삭제해도 에러가 발생하지 않는다.")
    @Test
    void deleteCuratedContent_AlreadyDeleted_NoError() {
        // Given
        Long contentId = 1L;
        Member member = MemberFixture.member(email, Role.ROLE_USER);
        Content content = ContentFixture.content("해리포터", "이미삭제됨");
        CuratedContent deletedCuratedContent = CuratedContentFixture.deletedCuratedContent(member,
                content);

        given(contentQuery.findCuratedContentByMemberIdAndContentId(member.getId(), contentId))
                .willReturn(Optional.of(deletedCuratedContent));

        // When & Then (예외 발생하지 않아야 함)
        contentService.deleteCuratedContent(member.getId(), contentId);

        verify(contentQuery).findCuratedContentByMemberIdAndContentId(member.getId(), contentId);
    }

    @DisplayName("존재하지 않는 엄선된 콘텐츠 삭제 시도시에도 에러가 발생하지 않는다.")
    @Test
    void deleteCuratedContent_NotExists_NoError() {
        // Given
        Long contentId = 1L;
        Member member = MemberFixture.member(email, Role.ROLE_USER);

        given(contentQuery.findCuratedContentByMemberIdAndContentId(member.getId(), contentId))
                .willReturn(Optional.empty());

        // When & Then (예외 발생하지 않아야 함)
        contentService.deleteCuratedContent(member.getId(), contentId);

        verify(contentQuery).findCuratedContentByMemberIdAndContentId(member.getId(), contentId);
    }
}