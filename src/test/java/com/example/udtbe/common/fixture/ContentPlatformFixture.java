package com.example.udtbe.common.fixture;

import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentPlatform;
import com.example.udtbe.domain.content.entity.Platform;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ContentPlatformFixture {

    public static List<ContentPlatform> contentPlatforms(Content content, int count) {
        List<ContentPlatform> list = new ArrayList<>();

        IntStream.rangeClosed(1, count).forEach(i -> {
            Platform platform = Platform.of(PlatformType.NETFLIX);
            ContentPlatform contentPlatform = ContentPlatform.of("https://example.com/watch" + i,
                    true, content,
                    platform);
            list.add(contentPlatform);
        });
        return list;
    }
}
