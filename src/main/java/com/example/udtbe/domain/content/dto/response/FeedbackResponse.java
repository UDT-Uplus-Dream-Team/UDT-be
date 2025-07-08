package com.example.udtbe.domain.content.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record FeedbackResponse(
        Long contentId,
        String title,
        String description,
        String posterUrl,
        String backdropUrl,
        LocalDateTime openDate,
        Integer runningTime,
        String rating,
        List<ContentCategoryResponseDTO> categories,
        List<ContentDirectorResponseDTO> directors,
        List<ContentPlatformResponseDTO> platforms
) {

}
