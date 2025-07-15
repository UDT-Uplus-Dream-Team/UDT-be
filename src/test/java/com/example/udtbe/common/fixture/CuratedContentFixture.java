package com.example.udtbe.common.fixture;

import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.CuratedContent;
import com.example.udtbe.domain.member.entity.Member;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class CuratedContentFixture {

    public static CuratedContent curatedContent(Member member, Content content) {
        return CuratedContent.of(
                false,
                member,
                content
        );
    }
}
