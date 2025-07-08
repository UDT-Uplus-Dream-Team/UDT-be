package com.example.udtbe.global.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;

@Converter
public class TagConverter extends CommonConverter implements
        AttributeConverter<List<String>, String> {

    private static final String DELIMITER = ",";

    @Override
    public List<String> convertToEntityAttribute(String databaseValue) {
        validateNotNull(databaseValue);
        return Arrays.asList(databaseValue.split(DELIMITER));
    }

    @Override
    public String convertToDatabaseColumn(List<String> tagList) {
        validateList(tagList);
        return String.join(DELIMITER, tagList);
    }
}
