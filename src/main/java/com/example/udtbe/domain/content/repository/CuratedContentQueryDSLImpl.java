package com.example.udtbe.domain.content.repository;

import static com.example.udtbe.domain.content.entity.QContent.content;
import static com.example.udtbe.domain.content.entity.QCuratedContent.curatedContent;

import com.example.udtbe.domain.member.dto.response.MemberCuratedContentGetResponse;
import com.example.udtbe.domain.member.dto.response.QMemberCuratedContentGetResponse;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.dto.CursorPageResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CuratedContentQueryDSLImpl implements CuratedContentQueryDSL {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public CursorPageResponse<MemberCuratedContentGetResponse> getCuratedContentByCursor(
            Long cursor, int size, Member member) {

        List<MemberCuratedContentGetResponse> curatedContentGetResponses = jpaQueryFactory
                .select(
                        new QMemberCuratedContentGetResponse(
                                content.id,
                                content.title,
                                content.posterUrl
                        )
                ).from(curatedContent)
                .join(curatedContent.content, content)
                .where(baseFilter(member).and(cursorFilter(cursor)))
                .orderBy(curatedContent.id.desc())
                .limit(size + 1)
                .fetch();

        if (curatedContentGetResponses == null) {
            curatedContentGetResponses = Collections.emptyList();
        }

        boolean hasNext = isNext(curatedContentGetResponses.size(), size);

        if (hasNext) {
            curatedContentGetResponses.remove(curatedContentGetResponses.size() - 1);
        }

        String nextCursor = hasNext ? String.valueOf(
                curatedContentGetResponses.get(curatedContentGetResponses.size() - 1).contentId())
                : null;

        return new CursorPageResponse<>(curatedContentGetResponses, nextCursor, hasNext);
    }

    private boolean isNext(int itemSize, int requestSize) {
        return itemSize > requestSize;
    }

    private BooleanExpression baseFilter(Member member) {
        return curatedContent.member.eq(member).and(curatedContent.isDeleted.isFalse());
    }

    private BooleanExpression cursorFilter(Long cursor) {
        if (Objects.isNull(cursor)) {
            return null;
        }

        return curatedContent.id.lt(cursor);
    }

}
