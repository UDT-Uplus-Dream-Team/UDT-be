package com.example.udtbe.global.util;

import com.example.udtbe.domain.content.entity.enums.CategoryType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CategoryTypeConverter extends CommonConverter implements
        AttributeConverter<CategoryType, String> {

    @Override
    public CategoryType convertToEntityAttribute(String categoryType) {
        validateNotNull(categoryType);
        return CategoryType.from(categoryType);
    }

    @Override
    public String convertToDatabaseColumn(CategoryType categoryType) {
        validateNotNull(categoryType);
        return categoryType.getType();
    }
}
