package com.example.udtbe.common.fixture;

import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import java.util.Arrays;
import java.util.List;
import lombok.NoArgsConstructor;
import org.springframework.test.util.ReflectionTestUtils;

@NoArgsConstructor(access = PRIVATE)
public class ContentMetadataFixture {

    public static ContentMetadata metadata(Content content, String platformTag, String genreTag) {
        return ContentMetadata.of(
                content.getTitle(),
                "15세 이상",
                List.of("MOVIE"),
                parseTagString(genreTag),
                parseTagString(platformTag),
                List.of("감독1", "감독2"),
                List.of("배우1", "배우2"),
                content
        );
    }

    public static ContentMetadata metadata(Content content, String platformTag, String genreTag,
            String directorTag) {
        return ContentMetadata.of(
                content.getTitle(),
                "15세 이상",
                List.of("MOVIE"),
                parseTagString(genreTag),
                parseTagString(platformTag),
                parseTagString(directorTag),
                List.of("배우1", "배우2"),
                content
        );
    }

    public static ContentMetadata netflixActionMetadata(Content content) {
        return ContentMetadata.of(
                content.getTitle(),
                "15세 이상",
                List.of("MOVIE"),
                List.of("ACTION", "DRAMA"),
                List.of("NETFLIX"),
                List.of("감독1", "감독2"),
                List.of("배우1", "배우2"),
                content
        );
    }

    public static ContentMetadata watchaComedyMetadata(Content content) {
        return ContentMetadata.of(
                content.getTitle(),
                "12세 이상",
                List.of("MOVIE"),
                List.of("COMEDY", "ROMANCE"),
                List.of("WATCHA"),
                List.of("감독3", "감독4"),
                List.of("배우3", "배우4"),
                content
        );
    }

    public static ContentMetadata netflixThrillerMetadata(Content content) {
        return ContentMetadata.of(
                content.getTitle(),
                "18세 이상",
                List.of("MOVIE"),
                List.of("드라마", "스릴러"),
                List.of("넷플릭스", "웨이브"),
                List.of("감독5", "감독6"),
                List.of("배우5", "배우6"),
                content
        );
    }

    public static ContentMetadata deletedMetadata(Content content) {
        return ContentMetadata.of(
                content.getTitle(),
                "전체 이용가",
                List.of("MOVIE"),
                List.of("기타"),
                List.of("기타플랫폼"),
                List.of("미상"),
                List.of("미상배우"),
                content
        );
    }

    public static ContentMetadata customMetadata(Content content, String title, String rating,
            String genreTag, String platformTag, String directorTag, List<String> categoryTag,
            List<String> castTag) {
        return ContentMetadata.of(
                title,
                rating,
                categoryTag,
                parseTagString(genreTag),
                parseTagString(platformTag),
                parseTagString(directorTag),
                castTag,
                content
        );
    }

    public static ContentMetadata dramaMetadata(Content content) {
        return ContentMetadata.of(
                content.getTitle(),
                "15세 이상",
                List.of("DRAMA"),
                List.of("로맨스", "멜로"),
                List.of("넷플릭스", "티빙"),
                List.of("드라마감독1"),
                List.of("드라마배우1", "드라마배우2"),
                content
        );
    }

    public static ContentMetadata animationMetadata(Content content) {
        return ContentMetadata.of(
                content.getTitle(),
                "전체 이용가",
                List.of("ANIMATION"),
                List.of("모험", "판타지"),
                List.of("디즈니플러스", "넷플릭스"),
                List.of("애니감독1"),
                List.of("성우1", "성우2"),
                content
        );
    }

    public static ContentMetadata varietyMetadata(Content content) {
        return ContentMetadata.of(
                content.getTitle(),
                "12세 이상",
                List.of("VARIETY"),
                List.of("예능", "리얼리티"),
                List.of("유튜브", "웨이브"),
                List.of("예능PD1"),
                List.of("출연자1", "출연자2"),
                content
        );
    }

    // === 실제 영화 데이터에 맞는 메타데이터 (ContentFixture와 연동) ===

    public static ContentMetadata parasiteMetadata() {
        Content content = ContentFixture.parasite(); // ID 1L 이미 설정됨
        return createMetadataWithId(content, "기생충", "15세이상관람가", 
                List.of("영화"), List.of("스릴러", "서사/드라마", "범죄"),
                List.of("넷플릭스", "왓챠"), List.of("봉준호"));
    }

    public static ContentMetadata oldboyMetadata() {
        Content content = ContentFixture.oldboy(); // ID 2L 이미 설정됨
        return createMetadataWithId(content, "올드보이", "청소년관람불가",
                List.of("영화"), List.of("스릴러", "미스터리", "범죄"),
                List.of("넷플릭스", "티빙"), List.of("박찬욱"));
    }

    public static ContentMetadata interstellarMetadata() {
        Content content = ContentFixture.interstellar(); // ID 3L 이미 설정됨
        return createMetadataWithId(content, "인터스텔라", "12세이상관람가",
                List.of("영화"), List.of("SF", "어드벤처", "서사/드라마"),
                List.of("넷플릭스", "왓챠", "웨이브"), List.of("크리스토퍼 놀란"));
    }

    public static ContentMetadata avatarMetadata() {
        Content content = ContentFixture.avatar(); // ID 4L 이미 설정됨
        return createMetadataWithId(content, "아바타: 물의 길", "12세이상관람가",
                List.of("영화"), List.of("SF", "어드벤처", "판타지"),
                List.of("디즈니+", "쿠팡플레이"), List.of("제임스 카메론"));
    }

    public static ContentMetadata topGunMetadata() {
        Content content = ContentFixture.topGun(); // ID 5L 이미 설정됨
        return createMetadataWithId(content, "탑건: 매버릭", "12세이상관람가",
                List.of("영화"), List.of("액션", "어드벤처", "서사/드라마"),
                List.of("넷플릭스", "쿠팡플레이", "Apple TV"), List.of("조셉 코신스키"));
    }

    public static ContentMetadata laLaLandMetadata() {
        Content content = ContentFixture.laLaLand(); // ID 6L 이미 설정됨
        return createMetadataWithId(content, "라라랜드", "12세이상관람가",
                List.of("영화"), List.of("뮤지컬", "멜로/로맨스", "서사/드라마"),
                List.of("넷플릭스", "왓챠", "티빙"), List.of("데미언 셔젤"));
    }

    public static ContentMetadata getOutMetadata() {
        Content content = ContentFixture.getOut(); // ID 7L 이미 설정됨
        return createMetadataWithId(content, "겟 아웃", "15세이상관람가",
                List.of("영화"), List.of("공포(호러),", "스릴러", "미스터리"),
                List.of("넷플릭스", "왓챠"), List.of("조던 필"));
    }

    public static ContentMetadata blackPantherMetadata() {
        Content content = ContentFixture.blackPanther(); // ID 8L 이미 설정됨
        return createMetadataWithId(content, "블랙 팬서", "12세이상관람가",
                List.of("영화"), List.of("액션", "어드벤처", "SF"),
                List.of("디즈니+", "넷플릭스"), List.of("라이언 쿠글러"));
    }

    public static ContentMetadata jokerMetadata() {
        Content content = ContentFixture.joker(); // ID 9L 이미 설정됨
        return createMetadataWithId(content, "조커", "15세이상관람가",
                List.of("영화"), List.of("스릴러", "서사/드라마", "범죄"),
                List.of("넷플릭스", "왓챠", "티빙"), List.of("토드 필립스"));
    }

    public static ContentMetadata spiderManMetadata() {
        Content content = ContentFixture.spiderMan(); // ID 10L 이미 설정됨
        return createMetadataWithId(content, "스파이더맨: 노 웨이 홈", "12세이상관람가",
                List.of("영화"), List.of("액션", "어드벤처", "SF"),
                List.of("넷플릭스", "쿠팡플레이"), List.of("존 왓츠"));
    }

    // === Helper 메서드 ===

    private static ContentMetadata createMetadataWithId(Content content, String title, String rating,
            List<String> categoryTag, List<String> genreTag,
            List<String> platformTag, List<String> directorTag) {
        ContentMetadata metadata = ContentMetadata.of(
                title,
                rating,
                categoryTag,
                genreTag,
                platformTag,
                directorTag,
                List.of("배우1", "배우2"), // 기본 캐스트
                content
        );
        // ContentMetadata ID는 Content ID와 다를 수 있으므로 별도 설정하지 않음
        return metadata;
    }

    // === 모든 메타데이터 리스트 반환 ===

    public static List<ContentMetadata> allTestMetadata() {
        return List.of(
                parasiteMetadata(), oldboyMetadata(), interstellarMetadata(), avatarMetadata(),
                topGunMetadata(), laLaLandMetadata(), getOutMetadata(), blackPantherMetadata(),
                jokerMetadata(), spiderManMetadata()
        );
    }

    // 문자열을 쉼표로 분리해서 List로 변환하는 헬퍼 메서드
    private static List<String> parseTagString(String tagString) {
        if (tagString == null || tagString.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(tagString.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .toList();
    }
}