package com.example.udtbe.global.util;

import com.example.udtbe.domain.content.entity.enums.PlatformType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PlatformTypeConverter extends CommonConverter implements
        AttributeConverter<PlatformType, String> {

    @Override
    public PlatformType convertToEntityAttribute(String platformType) {
        validateNotNull(platformType);
        return PlatformType.from(platformType);
    }

    @Override
    public String convertToDatabaseColumn(PlatformType platformType) {
        validateNotNull(platformType);
        return platformType.name();
    }
}
