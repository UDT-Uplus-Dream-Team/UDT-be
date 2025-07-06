package com.example.udtbe.global.util;

import com.example.udtbe.domain.member.entity.enums.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class RoleConverter extends CommonConverter implements AttributeConverter<Role, String> {

    @Override
    public Role convertToEntityAttribute(String role) {
        validateNotNull(role);
        return Role.from(role);
    }

    @Override
    public String convertToDatabaseColumn(Role role) {
        validateNotNull(role);
        return role.name();
    }
}
