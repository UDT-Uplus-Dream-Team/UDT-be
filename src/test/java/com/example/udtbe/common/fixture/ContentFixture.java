package com.example.udtbe.common.fixture;

import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.content.entity.Content;
import java.time.LocalDateTime;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class ContentFixture {

    public static Content content(String title) {
        return Content.of(
                title,
                "테스트 설명",
                "https://poster-url",
                "https://backdrop-url",
                "https://trailer-url",
                LocalDateTime.now(),
                120,
                0,
                null,
                false,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }
}