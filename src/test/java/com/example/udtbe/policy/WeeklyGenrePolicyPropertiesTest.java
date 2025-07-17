package com.example.udtbe.policy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.example.udtbe.common.support.ApiSupport;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.global.config.WeeklyGenrePolicyProperties;
import java.time.DayOfWeek;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application.yml")
@EnableConfigurationProperties(WeeklyGenrePolicyProperties.class)
class WeeklyGenrePolicyPropertiesTest extends ApiSupport {

    @Autowired
    WeeklyGenrePolicyProperties weeklyGenrePolicyProperties;

    @DisplayName("월요일 정책에 해당하는 장르를 가져온다.")
    @Test
    void getGenresForMondayPolicy() {
        // given
        DayOfWeek monday = DayOfWeek.MONDAY;

        // when
        List<GenreType> genreTypes = weeklyGenrePolicyProperties.getGenreForToday(monday);

        // then
        assertAll(
                () -> assertThat(genreTypes.size()).isEqualTo(2),
                () -> assertThat(genreTypes.get(0)).isEqualByComparingTo(GenreType.VARIETY),
                () -> assertThat(genreTypes.get(1)).isEqualByComparingTo(GenreType.COMEDY)
        );
    }

    @DisplayName("목요일 정책에 해당하는 장르를 가져온다.")
    @Test
    void getGenresForThursdayPolicy() {
        // given
        DayOfWeek monday = DayOfWeek.THURSDAY;

        // when
        List<GenreType> genreTypes = weeklyGenrePolicyProperties.getGenreForToday(monday);

        // then
        assertThat(genreTypes.size()).isZero();
    }
}
