package com.example.udtbe.common.fixture;

import static com.example.udtbe.domain.content.entity.enums.CategoryType.ANIMATION;
import static com.example.udtbe.domain.content.entity.enums.CategoryType.DRAMA;
import static com.example.udtbe.domain.content.entity.enums.CategoryType.MOVIE;
import static com.example.udtbe.domain.content.entity.enums.CategoryType.VARIETY;
import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.content.entity.Category;
import com.example.udtbe.domain.content.entity.enums.CategoryType;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class CategoryFixture {

    public static List<Category> categories() {
        List<CategoryType> platformTypes = List.of(
                MOVIE,
                DRAMA,
                ANIMATION,
                VARIETY
        );

        List<Category> categories = new ArrayList<>();
        for (CategoryType categoryType : platformTypes) {
            categories.add(Category.of((categoryType)));
        }

        return categories;
    }
}
