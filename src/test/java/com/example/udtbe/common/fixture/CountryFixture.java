package com.example.udtbe.common.fixture;

import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.content.entity.Country;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class CountryFixture {

    public static List<Country> countries() {
        List<String> countryNames = List.of(
                "한국",
                "일본",
                "중국",
                "미국",
                "대만",
                "프랑스",
                "홍콩",
                "이탈리아",
                "인도",
                "벨기에",
                "스웨덴"
        );

        List<Country> countries = new ArrayList<>();
        for (String countryName : countryNames) {
            countries.add(Country.of(countryName));
        }

        return countries;
    }
}
