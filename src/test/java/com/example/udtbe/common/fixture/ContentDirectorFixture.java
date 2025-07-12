package com.example.udtbe.common.fixture;

import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentDirector;
import com.example.udtbe.domain.content.entity.Director;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ContentDirectorFixture {

    public static List<ContentDirector> contentDirectors(Content content, int count) {
        List<ContentDirector> list = new ArrayList<>();
        IntStream.rangeClosed(1, count).forEach(i -> {
            Director director = Director.of("감스트" + i);
            ContentDirector contentDirector = ContentDirector.of(content, director);
            list.add(contentDirector);
        });
        return list;
    }
}
