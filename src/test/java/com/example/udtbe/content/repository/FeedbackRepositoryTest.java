package com.example.udtbe.content.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.FeedbackFixture;
import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.common.support.DataJpaSupport;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.domain.content.repository.FeedbackRepository;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.entity.enums.Role;
import com.example.udtbe.domain.member.repository.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

class FeedbackRepositoryTest extends DataJpaSupport {

    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    MemberRepository memberRepository;

    @DisplayName("인기 콘텐츠 목록 상위 8개 조회한다.")
    @Test
    void getPopularContents() {
        // given
        List<Integer> topRankedIndexes = List.of(3, 5, 8, 11, 14, 18, 21, 25);

        List<Content> savedContents = contentRepository.saveAll(ContentFixture.contents(30));
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            members.add(MemberFixture.member("email" + i, Role.ROLE_USER));
        }
        List<Member> savedMembers = memberRepository.saveAll(members);

        List<Feedback> feedbacks = new ArrayList<>();

        for (int i = 0; i < savedContents.size(); i++) {
            Content content = savedContents.get(i);
            for (int j = 0; j < 50; j++) {
                Member member = savedMembers.get(j);
                if (topRankedIndexes.contains(i)) {
                    if (j < 40) {
                        feedbacks.add(FeedbackFixture.like(member, content));
                    } else {
                        feedbacks.add(FeedbackFixture.dislike(member, content));
                    }
                } else {
                    if (j < 20) {
                        feedbacks.add(FeedbackFixture.dislike(member, content));
                    } else {
                        feedbacks.add(FeedbackFixture.like(member, content));
                    }
                }
            }
        }
        List<Feedback> savedFeedbacks = feedbackRepository.saveAll(feedbacks);

        // when
        List<Content> topRankedContents = feedbackRepository.findTopRankedContents(
                PageRequest.of(0, topRankedIndexes.size())
        );

        // then
        assertAll(
                () -> {
                    List<Long> expectedIds = topRankedIndexes.stream()
                            .map(i -> savedContents.get(i).getId())
                            .toList();

                    List<Long> actualIds = topRankedContents.stream()
                            .map(Content::getId)
                            .toList();

                    assertThat(actualIds).containsExactlyElementsOf(expectedIds);
                }
        );

    }
}
