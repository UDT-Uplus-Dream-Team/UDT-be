package com.example.udtbe.global.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.util.StringUtils;

@Converter
public class OptionalTagConverter extends CommonConverter implements
        AttributeConverter<List<String>, String> {

    private static final String DELIMITER = ",";

    @Override
    public List<String> convertToEntityAttribute(String databaseValue) {
        if (!StringUtils.hasText(databaseValue)) {
            return Collections.emptyList();
        }
        return Arrays.asList(databaseValue.split(DELIMITER));
    }

    @Override
    public String convertToDatabaseColumn(List<String> tagList) {
        if (Objects.isNull(tagList) || tagList.isEmpty()) {
            return null;
        }
        return String.join(DELIMITER, tagList);
    }
}
