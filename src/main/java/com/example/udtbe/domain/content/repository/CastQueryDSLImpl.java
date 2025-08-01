package com.example.udtbe.domain.content.repository;

import static com.example.udtbe.domain.content.entity.QCast.cast;

import com.example.udtbe.domain.admin.dto.request.AdminCastsGetRequest;
import com.example.udtbe.domain.admin.dto.response.AdminCastsGetResponse;
import com.example.udtbe.domain.admin.dto.response.QAdminCastsGetResponse;
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
public class CastQueryDSLImpl implements CastQueryDSL {

    private final JPAQueryFactory queryFactory;

    @Override
    public CursorPageResponse<AdminCastsGetResponse> getCasts(AdminCastsGetRequest request) {

        List<AdminCastsGetResponse> items = queryFactory
                .select(new QAdminCastsGetResponse(cast))
                .from(cast)
                .where(
                        isNotDeletedFilter(),
                        nameFilter(request.name()),
                        cursorFilter(request.cursor())
                )
                .orderBy(cast.castName.asc(), cast.id.asc())
                .limit(request.getSizeOrDefault() + 1)
                .fetch();

        boolean hasNext = isNext(items.size(), request.getSizeOrDefault());

        if (hasNext) {
            items.remove(items.size() - 1);
        }

        String nextCursor = hasNext ? String.valueOf(items.get(items.size() - 1).castId()) : null;

        return new CursorPageResponse<>(items, nextCursor, hasNext);
    }

    private BooleanExpression isNotDeletedFilter() {
        return cast.isDeleted.isFalse();
    }

    private boolean isNext(int itemSize, int requestSize) {
        return itemSize > requestSize;
    }

    private BooleanExpression cursorFilter(String cursorId) {
        return Optional.ofNullable(cursorId)
                .map(Long::valueOf)
                .map(cast.id::gt)
                .orElse(null);
    }

    private BooleanExpression nameFilter(String name) {
        return Optional.ofNullable(name)
                .filter(StringUtils::hasText)
                .map(cast.castName::containsIgnoreCase)
                .orElse(null);
    }
}
