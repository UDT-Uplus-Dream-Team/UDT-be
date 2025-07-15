package com.example.udtbe.domain.content.dto;

import com.example.udtbe.domain.content.entity.ContentCountry;
import java.util.List;

public class ContentCountryMapper {

    public static List<String> countryNames(List<ContentCountry> countries) {
        return countries.stream()
                .map(c -> c.getCountry().getCountryName())
                .toList();
    }
}
