package com.example.udtbe.domain.admin.dto.response;

import com.example.udtbe.domain.admin.dto.common.AdminCastDetailsDTO;
import com.example.udtbe.domain.admin.dto.common.AdminCategoryDTO;
import com.example.udtbe.domain.admin.dto.common.AdminDirectorDetailsDTO;
import com.example.udtbe.domain.admin.dto.common.AdminPlatformDTO;
import java.time.LocalDateTime;
import java.util.List;

public record AdminContentGetDetailResponse(
        String title,
        String description,
        String posterUrl,
        String backdropUrl,
        String trailerUrl,
        LocalDateTime openDate,
        Integer runningTime,
        Integer episode,
        String rating,
        List<AdminCategoryDTO> categories,
        List<String> countries,
        List<AdminDirectorDetailsDTO> directors,
        List<AdminCastDetailsDTO> casts,
        List<AdminPlatformDTO> platforms
) {

}
