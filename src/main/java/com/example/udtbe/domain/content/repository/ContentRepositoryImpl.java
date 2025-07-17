package com.example.udtbe.domain.content.repository;

import static com.example.udtbe.domain.content.entity.QCast.cast;
import static com.example.udtbe.domain.content.entity.QCategory.category;
import static com.example.udtbe.domain.content.entity.QContent.content;
import static com.example.udtbe.domain.content.entity.QContentCast.contentCast;
import static com.example.udtbe.domain.content.entity.QContentCategory.contentCategory;
import static com.example.udtbe.domain.content.entity.QContentCountry.contentCountry;
import static com.example.udtbe.domain.content.entity.QContentDirector.contentDirector;
import static com.example.udtbe.domain.content.entity.QContentGenre.contentGenre;
import static com.example.udtbe.domain.content.entity.QContentPlatform.contentPlatform;
import static com.example.udtbe.domain.content.entity.QCountry.country;
import static com.example.udtbe.domain.content.entity.QDirector.director;
import static com.example.udtbe.domain.content.entity.QGenre.genre;
import static com.example.udtbe.domain.content.entity.QPlatform.platform;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;


import com.example.udtbe.domain.admin.dto.common.AdminCastDTO;
import com.example.udtbe.domain.admin.dto.common.AdminCategoryDTO;
import com.example.udtbe.domain.admin.dto.common.AdminPlatformDTO;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetResponse;
import com.example.udtbe.domain.admin.dto.response.QAdminContentGetResponse;
import com.example.udtbe.domain.content.dto.request.ContentsGetRequest;
import com.example.udtbe.domain.content.dto.request.WeeklyRecommendationRequest;
import com.example.udtbe.domain.content.dto.response.*;
import com.example.udtbe.domain.content.entity.*;
import com.example.udtbe.domain.content.entity.enums.CategoryType;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
import com.example.udtbe.domain.content.exception.ContentErrorCode;
import com.example.udtbe.global.dto.CursorPageResponse;
import com.example.udtbe.global.exception.RestApiException;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;



@Repository
@RequiredArgsConstructor
public class ContentRepositoryImpl implements ContentRepositoryCustom {

    private static final String DELIMITER = "|";
    private final JPAQueryFactory queryFactory;

    @Override
    public AdminContentGetDetailResponse getAdminContentDetails(Long contentId) {

        Content findContent = queryFactory
                .selectFrom(content)
                .where(content.id.eq(contentId), content.isDeleted.isFalse())
                .fetchOne();

        if (Objects.isNull(findContent)) {
            throw new RestApiException(ContentErrorCode.CONTENT_NOT_FOUND);
        }

        List<AdminPlatformDTO> platforms = queryFactory
                .select(Projections.constructor(
                        AdminPlatformDTO.class,
                        platform.platformType.stringValue(),
                        contentPlatform.watchUrl
                ))
                .from(contentPlatform)
                .join(contentPlatform.platform, platform)
                .where(contentPlatform.content.id.eq(contentId))
                .fetch();

        platforms = platforms.stream().map(p -> new AdminPlatformDTO(
                PlatformType.from(p.platformType()).getType(),
                p.watchUrl()
        )).toList();

        List<AdminCategoryDTO> categories = queryFactory
                .from(contentCategory)
                .join(contentCategory.category, category)
                .leftJoin(contentGenre).on(contentGenre.content.id.eq(contentId)
                        .and(contentGenre.genre.category.id.eq(category.id)))
                .leftJoin(contentGenre.genre, genre)
                .where(contentGenre.content.id.eq(contentId))
                .transform(groupBy(category.categoryType).list(
                        Projections.constructor(
                                AdminCategoryDTO.class,
                                category.categoryType.stringValue(),
                                list(genre.genreType.stringValue())
                        )
                ));
        categories = categories.stream()
                .map(c -> new AdminCategoryDTO(
                        CategoryType.from(c.categoryType()).getType(),
                        c.genres().stream()
                                .map(g -> GenreType.from(g).getType()).toList()
                )).toList();

        List<String> countries = queryFactory
                .select(country.countryName.stringValue())
                .from(contentCountry)
                .where(contentCountry.content.id.eq(contentId))
                .fetch();

        List<String> directors = queryFactory
                .select(director.directorName.stringValue())
                .from(contentDirector)
                .where(contentDirector.content.id.eq(contentId))
                .fetch();

        List<AdminCastDTO> casts = queryFactory
                .select(Projections.constructor(
                        AdminCastDTO.class,
                        cast.castName,
                        cast.castImageUrl
                ))
                .from(contentCast)
                .join(contentCast.cast, cast)
                .where(contentCast.content.id.eq(contentId))
                .fetch();

        return new AdminContentGetDetailResponse(
                findContent.getTitle(),
                findContent.getDescription(),
                findContent.getPosterUrl(),
                findContent.getBackdropUrl(),
                findContent.getTrailerUrl(),
                findContent.getOpenDate(),
                findContent.getRunningTime(),
                findContent.getEpisode(),
                findContent.getRating(),
                categories,
                countries,
                directors,
                casts,
                platforms
        );
    }

    @Override
    public CursorPageResponse<AdminContentGetResponse> getsAdminContents(Long cursor,
            int size, String categoryType) {

        List<Long> contentIds = queryFactory
                .select(content.id)
                .from(content)
                .leftJoin(content.contentCategories, contentCategory)
                .leftJoin(contentCategory.category, category)
                .where(cursorFilter(cursor), deletedFilter(), categoryFilter(categoryType))
                .orderBy(content.openDate.desc(), content.id.desc())
                .limit(size + 1)
                .fetch();

        List<AdminContentGetResponse> contentAdminGetResponses = queryFactory
                .from(content)
                .leftJoin(content.contentCategories, contentCategory)
                .leftJoin(contentCategory.category, category)
                .leftJoin(content.contentPlatforms, contentPlatform)
                .leftJoin(contentPlatform.platform, platform)
                .where(content.id.in(contentIds))
                .orderBy(content.openDate.desc(), content.id.desc())
                .transform(
                        groupBy(content.id).list(
                                new QAdminContentGetResponse(
                                        content.id,
                                        content.title,
                                        content.posterUrl,
                                        content.openDate,
                                        content.rating,
                                        list(category.categoryType.stringValue()),
                                        list(platform.platformType.stringValue())
                                )
                        )
                );

        boolean hasNext = contentAdminGetResponses.size() > size;

        if (hasNext) {
            contentAdminGetResponses.remove(contentAdminGetResponses.size() - 1);
        }

        String nextCursor = hasNext ? String.valueOf(
                contentAdminGetResponses.get(contentAdminGetResponses.size() - 1).contentId())
                : null;

        return new CursorPageResponse<>(contentAdminGetResponses, nextCursor, hasNext);
    }

    @Override
    public CursorPageResponse<ContentsGetResponse> getContents(ContentsGetRequest request) {
        Long cursorId = null;
        LocalDateTime cursorOpenDate = null;

        String cursor = request.cursor();
        if (Objects.nonNull(cursor) && cursor.contains(DELIMITER)) {
            String[] parts = cursor.split("\\|");
            cursorId = Long.parseLong(parts[0]);
            cursorOpenDate = LocalDateTime.parse(parts[1]);
        }

        List<Long> allContentIds = queryFactory
                .select(content.id)
                .from(content)
                .fetch();

        List<Long> filteredByPlatForms = getContentIdsByPlatformTypes(
                request.platforms(), allContentIds);
        List<Long> filteredByCountries = getContentIdsByCountries(
                request.countries(), allContentIds);
        List<Long> filteredByOpenDates = getContentIdsByOpenDates(
                request.openDates(), allContentIds);
        List<Long> filteredRatings = getContentIdsByRatings(
                request.ratings(), allContentIds);
        List<Long> filteredCategories = getContentIdsByCategories(
                request.categories(), allContentIds);
        List<Long> filteredGenres = getContentIdsByGenres(
                request.genres(), allContentIds);

        Set<Long> filteredContentIds = new HashSet<>(filteredByPlatForms);
        filteredContentIds.retainAll(filteredByCountries);
        filteredContentIds.retainAll(filteredByOpenDates);
        filteredContentIds.retainAll(filteredRatings);
        filteredContentIds.retainAll(filteredCategories);
        filteredContentIds.retainAll(filteredGenres);

        List<ContentsGetResponse> items = queryFactory
                .select(new QContentsGetResponse(content))
                .from(content)
                .where(
                        deletedFilter(),
                        content.id.in(filteredContentIds),
                        complexCursorFilter(cursorOpenDate, cursorId)
                )
                .orderBy(content.openDate.desc(), content.id.desc())
                .limit(request.size() + 1)
                .fetch();

        boolean hasNext = isNext(items.size(), request.size());

        if (hasNext) {
            items.remove(items.size() - 1);
        }

        String nextCursor = hasNext ?
                items.get(items.size() - 1).contentId()
                        + DELIMITER
                        + fetchOpenDate(items.get(items.size() - 1).contentId())
                : null;

        return new CursorPageResponse<>(items, nextCursor, hasNext);
    }

    @Override
    public ContentDetailsGetResponse getContentDetails(Long contentId) {
        Content findContent = queryFactory
                .selectFrom(content)
                .where(content.id.eq(contentId), content.isDeleted.isFalse())
                .fetchOne();

        if (Objects.isNull(findContent)) {
            throw new RestApiException(ContentErrorCode.CONTENT_NOT_FOUND);
        }

        List<ContentPlatform> contentPlatforms = queryFactory
                .selectFrom(contentPlatform)
                .join(contentPlatform.platform, platform).fetchJoin()
                .where(contentPlatform.content.eq(findContent))
                .fetch();

        List<Cast> casts = queryFactory
                .select(cast)
                .from(contentCast)
                .join(contentCast.cast, cast)
                .where(contentCast.content.eq(findContent))
                .fetch();

        List<Director> directors = queryFactory
                .select(director)
                .from(contentDirector)
                .join(contentDirector.director, director)
                .where(contentDirector.content.eq(findContent))
                .fetch();

        List<Country> countries = queryFactory
                .select(country)
                .from(contentCountry)
                .join(contentCountry.country, country)
                .where(contentCountry.content.eq(findContent))
                .fetch();

        List<Category> categories = queryFactory
                .select(category)
                .from(contentCategory)
                .join(contentCategory.category, category)
                .where(contentCategory.content.eq(findContent))
                .fetch();

        List<Genre> genres = queryFactory
                .select(genre)
                .from(contentGenre)
                .join(contentGenre.genre, genre)
                .where(contentGenre.content.eq(findContent))
                .fetch();

        return new ContentDetailsGetResponse(
                findContent,
                contentPlatforms,
                casts,
                directors,
                countries,
                categories,
                genres
        );
    }

    @Override
    public List<WeeklyRecommendedContentsResponse> getWeeklyRecommendedContents(
            WeeklyRecommendationRequest request, List<GenreType> genreTypes) {

        List<WeeklyRecommendedContentsResponse> items = queryFactory
                .select(new QWeeklyRecommendedContentsResponse(content))
                .from(content)
                .leftJoin(content.contentGenres, contentGenre)
                .leftJoin(contentGenre.genre, genre)
                .where(
                        deletedFilter(),
                        genresFilter(genreTypes)
                )
                .orderBy(content.id.desc())
                .limit(request.size())
                .fetch();

        return items;
    }

    private List<Long> getContentIdsByPlatformTypes(List<String> platforms,
                                                    List<Long> allContentIds) {

        if (isNullOrEmpty(platforms)) {
            return allContentIds;
        }

        List<PlatformType> platformTypes = platforms.stream()
                .map(PlatformType::fromByType)
                .toList();

        return queryFactory
                .select(content.id)
                .from(content)
                .join(content.contentPlatforms, contentPlatform)
                .where(platform.platformType.in(platformTypes))
                .fetch();
    }

    private List<Long> getContentIdsByCountries(List<String> countries, List<Long> allContentIds) {

        if (isNullOrEmpty(countries)) {
            return allContentIds;
        }

        return queryFactory
                .select(content.id)
                .from(content)
                .join(content.contentCountries, contentCountry)
                .join(contentCountry.country, country)
                .where(country.countryName.in(countries))
                .fetch();
    }

    private List<Long> getContentIdsByRatings(List<String> ratings,
                                              List<Long> allContentIds) {

        if (isNullOrEmpty(ratings)) {
            return allContentIds;
        }
        return queryFactory
                .select(content.id)
                .from(content)
                .where(content.rating.in(ratings))
                .fetch();
    }

    private List<Long> getContentIdsByOpenDates(List<LocalDateTime> openDates,
                                                List<Long> allContentIds) {

        if (isNullOrEmpty(openDates)) {
            return allContentIds;
        }

        List<Integer> years = openDates.stream()
                .map(LocalDateTime::getYear)
                .distinct()
                .toList();

        return queryFactory
                .select(content.id)
                .from(content)
                .where(content.openDate.year().in(years))
                .fetch();
    }

    private List<Long> getContentIdsByCategories(List<String> categories,
                                                 List<Long> allContentIds) {

        if (isNullOrEmpty(categories)) {
            return allContentIds;
        }

        List<CategoryType> categoryTypes = categories.stream()
                .map(CategoryType::fromByType)
                .toList();

        return queryFactory
                .select(content.id)
                .from(content)
                .join(content.contentCategories, contentCategory)
                .join(contentCategory.category, category)
                .where(category.categoryType.in(categoryTypes))
                .fetch();
    }

    private List<Long> getContentIdsByGenres(List<String> genres,
                                             List<Long> allContentIds) {

        if (isNullOrEmpty(genres)) {
            return allContentIds;
        }

        List<GenreType> genreTypes = genres.stream()
                .map(GenreType::fromByType)
                .toList();

        return queryFactory
                .select(content.id)
                .from(content)
                .join(content.contentGenres, contentGenre)
                .join(contentGenre.genre, genre)
                .where(genre.genreType.in(genreTypes))
                .fetch();
    }

    private <T> boolean isNullOrEmpty(List<T> list) {
        return Objects.isNull(list) || list.isEmpty();
    }

    private BooleanExpression complexCursorFilter(LocalDateTime cursorOpenDate, Long cursorId) {
        if (cursorOpenDate == null || cursorId == null) {
            return null;
        }

        return content.openDate.lt(cursorOpenDate)
                .or(content.openDate.eq(cursorOpenDate).and(content.id.lt(cursorId)));
    }

    private boolean isNext(int itemSize, int requestSize) {
        return itemSize > requestSize;
    }

    private LocalDateTime fetchOpenDate(Long contentId) {
        return queryFactory
                .select(content.openDate)
                .from(content)
                .where(content.id.eq(contentId))
                .fetchOne();
    }

    private BooleanExpression genresFilter(List<GenreType> genreTypes) {
        if (Objects.isNull(genreTypes) || genreTypes.isEmpty()) {
            return null;
        }
        return genre.genreType.in(genreTypes);
    }

    private BooleanExpression cursorFilter(Long cursor) {
        if (Objects.isNull(cursor)) {
            return null;
        }
        return content.id.lt(cursor);
    }

    private BooleanExpression deletedFilter() {
        return content.isDeleted.isFalse();
    }

    private BooleanExpression categoryFilter(String categoryType) {
        if (Objects.isNull(categoryType) || categoryType.isBlank()) {
            return null;
        }
        CategoryType ct = CategoryType.fromByType(categoryType);
        return category.categoryType.in(ct);
    }
}
