package com.example.udtbe.global.util;

import com.example.udtbe.domain.member.entity.enums.Gender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class GenderConverter extends CommonConverter implements AttributeConverter<Gender, String> {

    @Override
    public Gender convertToEntityAttribute(String gender) {
        validateNotNull(gender);
        return Gender.from(gender);
    }

    @Override
    public String convertToDatabaseColumn(Gender gender) {
        validateNotNull(gender);
        return gender.name();
    }
}
