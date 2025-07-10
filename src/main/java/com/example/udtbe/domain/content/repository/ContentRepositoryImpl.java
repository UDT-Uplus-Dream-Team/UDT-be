package com.example.udtbe.domain.content.repository;

import static com.example.udtbe.domain.content.entity.QContent.content;

import com.example.udtbe.domain.admin.dto.common.ContentDTO;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.QContent;
import com.example.udtbe.global.dto.CursorPageResponse;
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
        BooleanExpression notDeletedCond = content.isDeleted.eq(false);

        List<Content> contents = queryFactory
                .select(new QContent(content))
                .from(content)
                .where(cursorCondition, notDeletedCond)
                .orderBy(content.id.desc())
                .limit(size + 1)
                .fetch();

        boolean hasNext = contents.size() > size;

        if (hasNext) {
            contents.remove(contents.size() - 1);
        }

        String nextCursor = hasNext ? String.valueOf(contents.get(contents.size() - 1).getId()) : null;

        List<ContentDTO> result = contents.stream()
                .map(content-> new ContentDTO(
                        content.getId(),
                        content.getTitle(),
                        content.getPosterUrl(),
                        content.getOpenDate(),
                        content.getRating()
                )).toList();

        return new CursorPageResponse<>(result, nextCursor, hasNext);
    }
}
