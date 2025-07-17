package com.example.udtbe.global.config;

import com.example.udtbe.domain.content.entity.enums.GenreType;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "weekly-genre-policy")
public class WeeklyGenrePolicyProperties {

    private final Map<DayOfWeek, List<GenreType>> genreByDay = new EnumMap<>(DayOfWeek.class);

    public List<GenreType> getGenreForToday(DayOfWeek today) {
        return genreByDay.getOrDefault(today, Collections.emptyList());
    }
}
