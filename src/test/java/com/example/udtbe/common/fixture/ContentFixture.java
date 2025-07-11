package com.example.udtbe.common.fixture;

import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.content.entity.Cast;
import com.example.udtbe.domain.content.entity.Category;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentCast;
import com.example.udtbe.domain.content.entity.ContentCategory;
import com.example.udtbe.domain.content.entity.ContentCountry;
import com.example.udtbe.domain.content.entity.ContentDirector;
import com.example.udtbe.domain.content.entity.ContentGenre;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.ContentPlatform;
import com.example.udtbe.domain.content.entity.Country;
import com.example.udtbe.domain.content.entity.Director;
import com.example.udtbe.domain.content.entity.Genre;
import com.example.udtbe.domain.content.entity.Platform;
import com.example.udtbe.domain.content.entity.enums.CategoryType;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class ContentFixture {
    public static Content content(String title, String description) {
        return Content.of(
                title,
                description,
                "https://example.com/default-poster.jpg",
                "https://example.com/default-backdrop.jpg",
                "https://example.com/default-trailer.mp4",
                LocalDateTime.now(),
                120,
                1,
                "전체관람가"
        );
    }

    public static Content content(String title, String description, String categoryName,
            String genreName, String platformName) {
        Content content = Content.of(
                title,
                description,
                "https://example.com/default-poster.jpg",
                "https://example.com/default-backdrop.jpg",
                "https://example.com/default-trailer.mp4",
                LocalDateTime.now(),
                120,
                1,
                "전체관람가"
        );

        Category category = Category.of(CategoryType.fromByType(categoryName));
        Genre genre = Genre.of(GenreType.fromByType(genreName), category);
        Platform platform = Platform.of(PlatformType.fromByType(platformName));
        Country country = Country.of("한국");
        Cast cast = Cast.of("배우A", "https://example.com/castA");
        Director director = Director.of("감독A");

        ContentCategory.of(content, category);
        ContentGenre.of(content, genre);
        ContentDirector.of(content, director);
        ContentCast.of(content, cast);
        ContentCountry.of(content, country);
        ContentPlatform.of("https://example.com/watch", true, content, platform);

        return content;
    }

    public static ContentMetadata contentMetadata(String title, Content content) {
        return ContentMetadata.of(
                title,
                "전체 관람가",
                List.of("영화"),
                List.of("장르1", "장르2", "장르3"),
                List.of("플렛폼1", "플렛폼2", "플렛폼3"),
                List.of("감스트1", "감스트2"),
                List.of("김원", "석"),
                content
        );
    }

    public static List<Content> contents(int count) {
        List<Content> list = new ArrayList<>();

        IntStream.rangeClosed(1, count).forEach(i -> {
            Content c = ContentFixture.content("반지의제왕" + i, "설명" + i);
            list.add(c);
        });

        return list;
    }
}
