package com.example.udtbe.common.fixture;

import static com.example.udtbe.domain.content.entity.enums.GenreType.ACTION;
import static com.example.udtbe.domain.content.entity.enums.GenreType.ADULT;
import static com.example.udtbe.domain.content.entity.enums.GenreType.ADVENTURE;
import static com.example.udtbe.domain.content.entity.enums.GenreType.ANIMATION;
import static com.example.udtbe.domain.content.entity.enums.GenreType.COMEDY;
import static com.example.udtbe.domain.content.entity.enums.GenreType.CRIME;
import static com.example.udtbe.domain.content.entity.enums.GenreType.DOCUMENTARY;
import static com.example.udtbe.domain.content.entity.enums.GenreType.DRAMA;
import static com.example.udtbe.domain.content.entity.enums.GenreType.FANTASY;
import static com.example.udtbe.domain.content.entity.enums.GenreType.HISTORICAL_DRAMA;
import static com.example.udtbe.domain.content.entity.enums.GenreType.HORROR;
import static com.example.udtbe.domain.content.entity.enums.GenreType.KIDS;
import static com.example.udtbe.domain.content.entity.enums.GenreType.MARTIAL_ARTS;
import static com.example.udtbe.domain.content.entity.enums.GenreType.MUSICAL;
import static com.example.udtbe.domain.content.entity.enums.GenreType.MYSTERY;
import static com.example.udtbe.domain.content.entity.enums.GenreType.REALITY;
import static com.example.udtbe.domain.content.entity.enums.GenreType.ROMANCE;
import static com.example.udtbe.domain.content.entity.enums.GenreType.SF;
import static com.example.udtbe.domain.content.entity.enums.GenreType.STAND_UP_COMEDY;
import static com.example.udtbe.domain.content.entity.enums.GenreType.SURVIVAL;
import static com.example.udtbe.domain.content.entity.enums.GenreType.TALK_SHOW;
import static com.example.udtbe.domain.content.entity.enums.GenreType.THRILLER;
import static com.example.udtbe.domain.content.entity.enums.GenreType.VARIETY;
import static com.example.udtbe.domain.content.entity.enums.GenreType.WESTERN;
import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.content.entity.Category;
import com.example.udtbe.domain.content.entity.Genre;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class GenreFixture {

    public static List<Genre> genres(Category category) {
        List<GenreType> genreTypes = List.of(
                ACTION,
                FANTASY,
                SF,
                THRILLER,
                MYSTERY,
                ADVENTURE,
                MUSICAL,
                COMEDY,
                WESTERN,
                ROMANCE,
                DRAMA,
                ANIMATION,
                HORROR,
                DOCUMENTARY,
                CRIME,
                MARTIAL_ARTS,
                HISTORICAL_DRAMA,
                ADULT,
                KIDS,
                VARIETY,
                TALK_SHOW,
                SURVIVAL,
                REALITY,
                STAND_UP_COMEDY
        );

        List<Genre> genres = new ArrayList<>();
        for (GenreType genreType : genreTypes) {
            genres.add(Genre.of(genreType, category));
        }

        return genres;
    }
}
