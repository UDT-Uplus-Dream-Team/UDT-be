package com.example.udtbe.global.util;

import com.example.udtbe.entity.enums.GenreType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class GenreTypeConverter extends CommonConverter implements
        AttributeConverter<GenreType, String> {

    @Override
    public GenreType convertToEntityAttribute(String genreType) {
        validateNotNull(genreType);
        return GenreType.from(genreType);
    }

    @Override
    public String convertToDatabaseColumn(GenreType genreType) {
        validateNotNull(genreType);
        return genreType.getType();
    }
}
