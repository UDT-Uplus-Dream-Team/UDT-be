package com.example.udtbe.global.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

@Converter
public class OptionalLongConverter extends CommonConverter implements
        AttributeConverter<List<Long>, String> {

    private static final String DELIMITER = ",";

    @Override
    public List<Long> convertToEntityAttribute(String databaseValue) {
        if (!StringUtils.hasText(databaseValue)) {
            return Collections.emptyList();
        }

        return Arrays.stream(databaseValue.split(DELIMITER))
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    @Override
    public String convertToDatabaseColumn(List<Long> longList) {
        if (Objects.isNull(longList) || longList.isEmpty()) {
            return null;
        }
        return longList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(DELIMITER));
    }
}
