package com.example.udtbe.domain.admin.dto.response;

import com.example.udtbe.domain.admin.dto.common.AdminCategoryDTO;
import com.example.udtbe.domain.admin.dto.common.AdminPlatformDTO;
import java.time.LocalDateTime;
import java.util.List;

public record AdminContentUpJobGetDetailResponse(

        Long batchJobMetricId,

        Long contentId,

        String title,

        String description,

        String posterUrl,

        String backdropUrl,

        String trailerUrl,

        LocalDateTime openDate,

        int runningTime,

        int episode,

        String rating,

        List<AdminCategoryDTO> categories,

        List<String> countries,

        List<Long> directors,

        List<Long> casts,

        List<AdminPlatformDTO> platforms,

        String errorCode,

        String errorMessage,

        int retryCount,

        int skipCount
) {

}