package com.example.udtbe.common.fixture;

import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentCategory;
import com.example.udtbe.domain.content.entity.ContentGenre;
import com.example.udtbe.domain.content.entity.Genre;
import com.example.udtbe.domain.content.entity.enums.CategoryType;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import java.util.ArrayList;
import java.util.List;

public class ContentGenreFixture {

    public static List<ContentGenre> contentGenres(List<ContentCategory> contentCategories,
            Content content) {
        List<ContentGenre> list = new ArrayList<>();

        contentCategories.forEach(c -> {
            if (c.getCategory().getCategoryType().equals(CategoryType.DRAMA)) {
                Genre genre1 = Genre.of(GenreType.DRAMA, c.getCategory());
                Genre genre2 = Genre.of(GenreType.ROMANCE, c.getCategory());
                ContentGenre contentGenre1 = ContentGenre.of(content, genre1);
                ContentGenre contentGenre2 = ContentGenre.of(content, genre2);
                list.add(contentGenre1);
                list.add(contentGenre2);
            } else if (c.getCategory().getCategoryType().equals(CategoryType.MOVIE)) {
                Genre genre1 = Genre.of(GenreType.ACTION, c.getCategory());
                Genre genre2 = Genre.of(GenreType.COMEDY, c.getCategory());
                Genre genre3 = Genre.of(GenreType.ROMANCE, c.getCategory());
                ContentGenre contentGenre1 = ContentGenre.of(content, genre1);
                ContentGenre contentGenre2 = ContentGenre.of(content, genre2);
                ContentGenre contentGenre3 = ContentGenre.of(content, genre3);
                list.add(contentGenre1);
                list.add(contentGenre2);
                list.add(contentGenre3);
            } else if (c.getCategory().getCategoryType().equals(CategoryType.ANIMATION)) {
                Genre genre1 = Genre.of(GenreType.KIDS, c.getCategory());
                ContentGenre contentGenre1 = ContentGenre.of(content, genre1);
                list.add(contentGenre1);
            }
        });
        return list;
    }

    public static ContentGenre contentGenre(Content content, Genre genre) {
        return ContentGenre.of(content, genre);
    }
}
