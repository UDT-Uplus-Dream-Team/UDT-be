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
import java.util.Arrays;
import java.util.List;
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

    @DisplayName("여러 엄선된 콘텐츠를 한 번에 삭제할 수 있다.")
    @Test
    void deleteCuratedContentsBulk_Success() {
        // Given
        List<Long> contentIds = Arrays.asList(1L, 2L, 3L);
        Member member = MemberFixture.member(email, Role.ROLE_USER);
        Content content1 = ContentFixture.content("해리포터1", "대량삭제1");
        Content content2 = ContentFixture.content("해리포터2", "대량삭제2");
        Content content3 = ContentFixture.content("해리포터3", "대량삭제3");

        CuratedContent curatedContent1 = CuratedContentFixture.activeCuratedContent(member,
                content1);
        CuratedContent curatedContent2 = CuratedContentFixture.activeCuratedContent(member,
                content2);
        CuratedContent curatedContent3 = CuratedContentFixture.activeCuratedContent(member,
                content3);

        List<CuratedContent> curatedContents = Arrays.asList(curatedContent1, curatedContent2,
                curatedContent3);

        given(contentQuery.findCuratedContentsByMemberIdAndContentIds(member.getId(), contentIds))
                .willReturn(curatedContents);

        // When
        contentService.deleteCuratedContents(member.getId(), contentIds);

        // Then
        verify(contentQuery).findCuratedContentsByMemberIdAndContentIds(member.getId(), contentIds);
        assertThat(curatedContent1.isDeleted()).isTrue();
        assertThat(curatedContent2.isDeleted()).isTrue();
        assertThat(curatedContent3.isDeleted()).isTrue();
    }

    @DisplayName("일부는 이미 삭제된 콘텐츠가 포함된 대량 삭제에서는 활성 콘텐츠만 삭제된다.")
    @Test
    void deleteCuratedContentsBulk_WithSomeDeleted_OnlyActiveDeleted() {
        // Given
        List<Long> contentIds = Arrays.asList(1L, 2L, 3L);
        Member member = MemberFixture.member(email, Role.ROLE_USER);
        Content content1 = ContentFixture.content("해리포터1", "혼합삭제1");
        Content content2 = ContentFixture.content("해리포터2", "혼합삭제2");
        Content content3 = ContentFixture.content("해리포터3", "혼합삭제3");

        CuratedContent activeCuratedContent = CuratedContentFixture.activeCuratedContent(member,
                content1);
        CuratedContent deletedCuratedContent = CuratedContentFixture.deletedCuratedContent(member,
                content2);
        CuratedContent anotherActiveCuratedContent = CuratedContentFixture.activeCuratedContent(
                member, content3);

        List<CuratedContent> curatedContents = Arrays.asList(activeCuratedContent,
                deletedCuratedContent, anotherActiveCuratedContent);

        given(contentQuery.findCuratedContentsByMemberIdAndContentIds(member.getId(), contentIds))
                .willReturn(curatedContents);

        // When
        contentService.deleteCuratedContents(member.getId(), contentIds);

        // Then
        verify(contentQuery).findCuratedContentsByMemberIdAndContentIds(member.getId(), contentIds);
        assertThat(activeCuratedContent.isDeleted()).isTrue();
        assertThat(deletedCuratedContent.isDeleted()).isTrue(); // 이미 삭제된 상태 유지
        assertThat(anotherActiveCuratedContent.isDeleted()).isTrue();
    }

    @DisplayName("존재하지 않는 콘텐츠 ID가 포함된 대량 삭제도 에러 없이 처리된다.")
    @Test
    void deleteCuratedContentsBulk_WithNonExistentIds_NoError() {
        // Given
        List<Long> contentIds = Arrays.asList(999L, 1000L);
        Member member = MemberFixture.member(email, Role.ROLE_USER);

        given(contentQuery.findCuratedContentsByMemberIdAndContentIds(member.getId(), contentIds))
                .willReturn(Arrays.asList()); // 빈 리스트 반환

        // When & Then (예외 발생하지 않아야 함)
        contentService.deleteCuratedContents(member.getId(), contentIds);

        verify(contentQuery).findCuratedContentsByMemberIdAndContentIds(member.getId(), contentIds);
    }
}