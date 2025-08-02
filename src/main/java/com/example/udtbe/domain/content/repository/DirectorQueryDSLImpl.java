package com.example.udtbe.domain.content.repository;

import static com.example.udtbe.domain.content.entity.QDirector.director;

import com.example.udtbe.domain.admin.dto.request.AdminDirectorsGetRequest;
import com.example.udtbe.domain.admin.dto.response.AdminDirectorsGetResponse;
import com.example.udtbe.domain.admin.dto.response.QAdminDirectorsGetResponse;
import com.example.udtbe.global.dto.CursorPageResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class DirectorQueryDSLImpl implements DirectorQueryDSL {

    private final JPAQueryFactory queryFactory;

    @Override
    public CursorPageResponse<AdminDirectorsGetResponse> getDirectors(
            AdminDirectorsGetRequest request) {
        List<AdminDirectorsGetResponse> items = queryFactory
                .select(new QAdminDirectorsGetResponse(director))
                .from(director)
                .where(
                        isNotDeletedFilter(),
                        nameFilter(request.name()),
                        cursorFilter(request.cursor())
                )
                .orderBy(director.directorName.asc(), director.id.asc())
                .limit(request.getSizeOrDefault() + 1)
                .fetch();

        boolean hasNext = isNext(items.size(), request.getSizeOrDefault());

        if (hasNext) {
            items.remove(items.size() - 1);
        }

        String nextCursor =
                hasNext ? String.valueOf(items.get(items.size() - 1).directorId()) : null;

        return new CursorPageResponse<>(items, nextCursor, hasNext);
    }

    private BooleanExpression isNotDeletedFilter() {
        return director.isDeleted.isFalse();
    }

    private boolean isNext(int itemSize, int requestSize) {
        return itemSize > requestSize;
    }

    private BooleanExpression cursorFilter(String cursorId) {
        return Optional.ofNullable(cursorId)
                .map(Long::valueOf)
                .map(director.id::gt)
                .orElse(null);
    }

    private BooleanExpression nameFilter(String name) {
        return Optional.ofNullable(name)
                .filter(StringUtils::hasText)
                .map(director.directorName::containsIgnoreCase)
                .orElse(null);
    }
}
