package com.example.udtbe.domain.content.dto;

import com.example.udtbe.domain.content.dto.response.ContentDirectorResponseDTO;
import com.example.udtbe.domain.content.entity.ContentDirector;
import java.util.List;

public class ContentDirectorMapper {

    public static List<ContentDirectorResponseDTO> toDtoList(List<ContentDirector> directors) {
        return directors.stream()
                .map(d -> new ContentDirectorResponseDTO(d.getId(), d.getDirector().toString()))
                .toList();
    }

}
