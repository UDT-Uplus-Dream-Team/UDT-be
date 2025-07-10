package com.example.udtbe.domain.admin.dto.common;

import java.time.LocalDateTime;

public record ContentDTO(
    Long contentId,
    String title,
    String posterUrl,
    LocalDateTime openDate,
    String rating
) {
}
