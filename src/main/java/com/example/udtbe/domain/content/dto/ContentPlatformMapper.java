package com.example.udtbe.domain.content.dto;

import com.example.udtbe.domain.content.entity.ContentPlatform;
import java.util.List;

public class ContentPlatformMapper {

    public static List<String> platformNames(List<ContentPlatform> platform) {
        return platform.stream()
                .map(c -> c.getPlatform().getPlatformType().getType())
                .toList();
    }
}
