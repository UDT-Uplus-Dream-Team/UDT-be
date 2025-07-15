package com.example.udtbe.common.fixture;

import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentCountry;
import com.example.udtbe.domain.content.entity.Country;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ContentCountryFixture {

    public static List<ContentCountry> contentCountries(Content content, int count) {
        List<ContentCountry> list = new ArrayList<>();

        IntStream.rangeClosed(1, count).forEach(i -> {
            Country country = Country.of("대한민국" + i);
            ContentCountry contentCountry = ContentCountry.of(content, country);
            list.add(contentCountry);
        });
        return list;
    }

    public static ContentCountry contentCountry(Content content, Country country) {
        return ContentCountry.of(content, country);
    }
}
