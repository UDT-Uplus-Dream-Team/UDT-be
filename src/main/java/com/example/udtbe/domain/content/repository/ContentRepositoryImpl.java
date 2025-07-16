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

import com.example.udtbe.domain.admin.dto.common.ContentDTO;
import com.example.udtbe.domain.content.dto.request.ContentsGetRequest;
import com.example.udtbe.domain.content.dto.request.WeeklyRecommendationRequest;
import com.example.udtbe.domain.content.dto.response.ContentDetailsGetResponse;
import com.example.udtbe.domain.content.dto.response.ContentsGetResponse;
import com.example.udtbe.domain.content.dto.response.QContentsGetResponse;
import com.example.udtbe.domain.content.dto.response.QWeeklyRecommendedContentsResponse;
import com.example.udtbe.domain.content.dto.response.WeeklyRecommendedContentsResponse;
import com.example.udtbe.domain.content.entity.Cast;
import com.example.udtbe.domain.content.entity.Category;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentPlatform;
import com.example.udtbe.domain.content.entity.Country;
import com.example.udtbe.domain.content.entity.Director;
import com.example.udtbe.domain.content.entity.Genre;
import com.example.udtbe.domain.content.entity.enums.CategoryType;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
import com.example.udtbe.domain.content.exception.ContentErrorCode;
import com.example.udtbe.global.dto.CursorPageResponse;
import com.example.udtbe.global.exception.RestApiException;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentRepositoryImpl implements ContentRepositoryCustom {

    private static final String DELIMITER = "|";
    private final JPAQueryFactory queryFactory;

    @Override
    public CursorPageResponse<ContentDTO> findContentsAdminByCursor(Long cursor, int size) {

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
                .where(cursorFilter(cursor), deletedFilter())
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
                request.contentSearchConditionDTO().platforms(), allContentIds);
        List<Long> filteredByCountries = getContentIdsByCountries(
                request.contentSearchConditionDTO().countries(), allContentIds);
        List<Long> filteredByOpenDates = getContentIdsByOpenDates(
                request.contentSearchConditionDTO().openDates(), allContentIds);
        List<Long> filteredRatings = getContentIdsByRatings(
                request.contentSearchConditionDTO().ratings(), allContentIds);
        List<Long> filteredCategories = getContentIdsByCategories(
                request.contentSearchConditionDTO().categories(), allContentIds);
        List<Long> filteredGenres = getContentIdsByGenres(
                request.contentSearchConditionDTO().genres(), allContentIds);

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
                .on(contentPlatform.isAvailable.isTrue())
                .join(contentPlatform.platform, platform)
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
        return genre.genreType.in(genreTypes);
    }

    private BooleanExpression cursorFilter(Long cursor) {
        if (Objects.isNull(cursor)) {
            return null;
        }

        return content.id.lt(cursor);
    }

    private BooleanExpression deletedFilter() {
        return content.isDeleted.eq(false);
    }
}
