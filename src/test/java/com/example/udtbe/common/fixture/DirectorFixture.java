package com.example.udtbe.common.fixture;

import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.content.entity.Director;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class DirectorFixture {

    public static Director director(String directorName) {
        return Director.of(
                directorName
        );
    }

    public static List<Director> directors() {
        return List.of(
                director("봉준호"),
                director("하정우"),
                director("크리스토퍼놀란"),
                director("김원석")
        );
    }
}
