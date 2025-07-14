package com.example.udtbe.domain.admin.dto.response;

import com.example.udtbe.domain.admin.dto.common.CastDTO;
import com.example.udtbe.domain.admin.dto.common.CategoryDTO;
import com.example.udtbe.domain.admin.dto.common.PlatformDTO;
import java.time.LocalDateTime;
import java.util.List;

public record ContentGetDetailResponse(
        String title,
        String description,
        String posterUrl,
        String backdropUrl,
        String trailerUrl,
        LocalDateTime openDate,
        Integer runningTime,
        Integer episode,
        String rating,
        List<CategoryDTO> categories,
        List<String> countries,
        List<String> directors,
        List<CastDTO> casts,
        List<PlatformDTO> platforms
) {

}
