package com.example.udtbe.common.fixture;

import com.example.udtbe.domain.content.entity.Category;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentCategory;
import com.example.udtbe.domain.content.entity.enums.CategoryType;
import java.util.ArrayList;
import java.util.List;

public class ContentCategoryFixture {

    public static List<ContentCategory> contentCategories(Content content) {
        List<ContentCategory> list = new ArrayList<>();
        Category category1 = Category.of(CategoryType.DRAMA);
        Category category2 = Category.of(CategoryType.MOVIE);
        Category category3 = Category.of(CategoryType.ANIMATION);
        ContentCategory contentCategory1 = ContentCategory.of(content, category1);
        ContentCategory contentCategory2 = ContentCategory.of(content, category2);
        ContentCategory contentCategory3 = ContentCategory.of(content, category3);

        list.add(contentCategory1);
        list.add(contentCategory2);
        list.add(contentCategory3);
        return list;
    }
}
