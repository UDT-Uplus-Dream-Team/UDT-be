package com.example.udtbe.domain.content.dto;

import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.content.entity.Cast;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class CastMapper {

    public static Cast toCast(String castName, String castImageUrl) {
        return Cast.of(castName, castImageUrl);
    }

}
