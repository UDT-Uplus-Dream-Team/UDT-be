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
import org.springframework.test.util.ReflectionTestUtils;

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
    // === ID가 미리 설정된 실제 영화 데이터 (1L~10L) ===

    public static Content parasite() {
        return createMovieWithId(1L, "기생충", "전 세계를 놀라게 한 봉준호 감독의 걸작",
                "15세이상관람가", LocalDateTime.of(2019, 5, 30, 0, 0), 132);
    }

    public static Content oldboy() {
        return createMovieWithId(2L, "올드보이", "박찬욱 감독의 복수 스릴러",
                "청소년관람불가", LocalDateTime.of(2003, 11, 21, 0, 0), 120);
    }

    public static Content interstellar() {
        return createMovieWithId(3L, "인터스텔라", "크리스토퍼 놀란의 SF 대작",
                "12세이상관람가", LocalDateTime.of(2014, 11, 6, 0, 0), 169);
    }

    public static Content avatar() {
        return createMovieWithId(4L, "아바타: 물의 길", "제임스 카메론의 아바타 속편",
                "12세이상관람가", LocalDateTime.of(2022, 12, 14, 0, 0), 192);
    }

    public static Content topGun() {
        return createMovieWithId(5L, "탑건: 매버릭", "톰 크루즈의 액션 블록버스터",
                "12세이상관람가", LocalDateTime.of(2022, 6, 22, 0, 0), 130);
    }

    public static Content laLaLand() {
        return createMovieWithId(6L, "라라랜드", "뮤지컬 로맨스의 대작",
                "12세이상관람가", LocalDateTime.of(2016, 12, 7, 0, 0), 128);
    }

    public static Content getOut() {
        return createMovieWithId(7L, "겟 아웃", "조던 필의 사회풍자 호러",
                "15세이상관람가", LocalDateTime.of(2017, 5, 17, 0, 0), 104);
    }

    public static Content blackPanther() {
        return createMovieWithId(8L, "블랙 팬서", "MCU의 아프리카 슈퍼히어로",
                "12세이상관람가", LocalDateTime.of(2018, 2, 14, 0, 0), 134);
    }

    public static Content joker() {
        return createMovieWithId(9L, "조커", "호아킨 피닉스의 빌런 오리진",
                "15세이상관람가", LocalDateTime.of(2019, 10, 2, 0, 0), 122);
    }

    public static Content spiderMan() {
        return createMovieWithId(10L, "스파이더맨: 노 웨이 홈", "멀티버스 스파이더맨",
                "12세이상관람가", LocalDateTime.of(2021, 12, 15, 0, 0), 148);
    }

    // === Helper 메서드 ===

    private static Content createMovieWithId(Long id, String title, String description,
            String rating, LocalDateTime openDate, int runningTime) {
        Content content = Content.of(
                title,
                description,
                "https://example.com/poster/" + id + ".jpg",
                "https://example.com/backdrop/" + id + ".jpg",
                "https://example.com/trailer/" + id + ".mp4",
                openDate,
                runningTime,
                1,
                rating
        );
        ReflectionTestUtils.setField(content, "id", id);
        return content;
    }

    // === 모든 영화 리스트 반환 ===

    public static List<Content> allTestMovies() {
        return List.of(
                parasite(), oldboy(), interstellar(), avatar(), topGun(),
                laLaLand(), getOut(), blackPanther(), joker(), spiderMan()
        );
    }
}
