package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.dto.request.CuratedContentGetRequest;
import com.example.udtbe.domain.content.entity.CuratedContent;
import com.example.udtbe.domain.content.entity.QCuratedContent;
import com.example.udtbe.domain.member.entity.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CuratedContentQueryDSLImpl implements CuratedContentQueryDSL {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CuratedContent> getCuratedContentByCursor(
            CuratedContentGetRequest curatedContentGetRequest, Member member) {
        QCuratedContent curatedContent = QCuratedContent.curatedContent;

        BooleanExpression baseCondition = curatedContent.member.eq(member)
                .and(curatedContent.isDeleted.isFalse());

        BooleanExpression cursorCondition = curatedContentGetRequest.cursor() != null
                ? curatedContent.id.lt(curatedContentGetRequest.cursor())
                : null;

        return jpaQueryFactory.selectFrom(curatedContent)
                .where(baseCondition.and(
                        cursorCondition != null ? cursorCondition : Expressions.TRUE))
                .orderBy(curatedContent.id.desc())
                .limit(curatedContentGetRequest.size() + 1)
                .fetch();
    }
}
