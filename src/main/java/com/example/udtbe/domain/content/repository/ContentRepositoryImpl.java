package com.example.udtbe.domain.content.repository;

import static com.example.udtbe.domain.content.entity.QContent.content;

import com.example.udtbe.domain.admin.dto.common.ContentDTO;
import com.example.udtbe.global.dto.CursorPageResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentRepositoryImpl implements ContentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public CursorPageResponse<ContentDTO> findContentsAdminByCursor(Long cursor, int size) {

        BooleanExpression cursorCondition = (cursor != null) ? content.id.lt(cursor) : null;
        BooleanExpression notDeleted = content.isDeleted.eq(false);

        List<ContentDTO> dtos = queryFactory
                .select(Projections.constructor(
                        ContentDTO.class,
                        content.id,
                        content.title,
                        content.posterUrl,
                        content.openDate,
                        content.rating
                ))
                .from(content)
                .where(cursorCondition, notDeleted)
                .orderBy(content.id.desc())
                .limit(size + 1)
                .fetch();

        boolean hasNext = dtos.size() > size;
        if (hasNext) {
            dtos.remove(dtos.size() - 1);
        }

        String nextCursor = hasNext
                ? String.valueOf(dtos.get(dtos.size() - 1).contentId())
                : null;

        return new CursorPageResponse<>(dtos, nextCursor, hasNext);
    }
}
