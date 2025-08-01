package com.example.udtbe.domain.content.dto;

import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.content.entity.Director;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class DirectorMapper {

    public static Director toDirector(String directorName, String directorImageUrl) {
        return Director.of(directorName, directorImageUrl);
    }
}
