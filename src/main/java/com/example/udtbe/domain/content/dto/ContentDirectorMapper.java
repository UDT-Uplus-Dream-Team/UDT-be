package com.example.udtbe.domain.content.dto;

import com.example.udtbe.domain.content.entity.ContentDirector;
import java.util.List;

public class ContentDirectorMapper {

    public static List<String> directorNames(List<ContentDirector> directors) {
        return directors.stream()
                .map(d -> d.getDirector().getDirectorName())
                .toList();
    }

}
