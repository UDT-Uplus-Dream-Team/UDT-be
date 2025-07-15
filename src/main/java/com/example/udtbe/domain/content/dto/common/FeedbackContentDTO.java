package com.example.udtbe.domain.content.dto.common;

import java.time.LocalDateTime;
import java.util.List;

public record FeedbackContentDTO(
        Long contentId,
        String title,
        String posterUrl,
        LocalDateTime openDate,
        Integer runningTime,
        Integer episode,
        List<String> categories,
        List<String> directors
) {

}
