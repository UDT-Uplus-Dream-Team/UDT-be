package com.example.udtbe.common.fixture;

import com.example.udtbe.domain.content.entity.Cast;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentCast;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ContentCastFixture {

    public static List<ContentCast> contentCasts(Content content, int count) {
        List<ContentCast> list = new ArrayList<>();

        IntStream.rangeClosed(1, count).forEach(i -> {
            Cast cast = Cast.of("박연진" + i, "https://example.com/cast" + i);
            ContentCast contentCast = ContentCast.of(content, cast);
            list.add(contentCast);
        });
        return list;
    }

    public static ContentCast contentCast(Content content, Cast cast) {
        return ContentCast.of(content, cast);
    }
}
