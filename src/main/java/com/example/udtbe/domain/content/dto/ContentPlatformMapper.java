package com.example.udtbe.domain.content.dto;

import com.example.udtbe.domain.content.dto.response.ContentPlatformResponseDTO;
import com.example.udtbe.domain.content.entity.ContentPlatform;
import java.util.List;

public class ContentPlatformMapper {

    public static List<ContentPlatformResponseDTO> toDtoList(List<ContentPlatform> platforms) {
        return platforms.stream()
                .map(p -> new ContentPlatformResponseDTO(p.getId(), p.getPlatform().toString()))
                .toList();
    }

}
