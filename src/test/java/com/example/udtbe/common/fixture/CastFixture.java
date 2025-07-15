package com.example.udtbe.common.fixture;

import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.content.entity.Cast;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class CastFixture {

    public static Cast cast(String castName) {
        return Cast.of(
                castName,
                "http://" + castName + ".png"
        );
    }

    public static List<Cast> casts() {
        return List.of(
                cast("마동석"),
                cast("황정민"),
                cast("토니스타크"),
                cast("수지"),
                cast("김원석")
        );
    }

}
