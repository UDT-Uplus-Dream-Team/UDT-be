package com.example.udtbe.common.fixture;

import com.example.udtbe.domain.admin.dto.common.AdminCategoryDTO;
import com.example.udtbe.domain.admin.dto.common.AdminPlatformDTO;
import com.example.udtbe.domain.batch.entity.AdminContentUpdateJob;
import com.example.udtbe.domain.batch.entity.enums.BatchStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminContentUpdateJobFixture {

    public static AdminContentUpdateJob createPendingJob(Long memberId, Long contentId,
            String title,
            String description) {
        Map<String, AdminCategoryDTO> adminCategoryDTOs = new HashMap<>();
        List<String> genre1 = new ArrayList<>();
        List<String> genre2 = new ArrayList<>();

        Map<String, AdminPlatformDTO> adminPlatformDTOs = new HashMap<>();

        genre1.add("SF");
        genre1.add("판타지");
        adminCategoryDTOs.put("영화", new AdminCategoryDTO("영화", genre1));

        genre2.add("키즈");
        adminCategoryDTOs.put("애니메이션", new AdminCategoryDTO("애니메이션", genre2));

        adminPlatformDTOs.put("넷플릭스", new AdminPlatformDTO("넷플릭스", "netflix.url"));

        return AdminContentUpdateJob.of(
                BatchStatus.PENDING,
                memberId,
                contentId,
                title,
                description,
                "poster.url",
                "backdrop.url",
                "trailer.url",
                LocalDateTime.now(),
                120,
                1,
                "12+",
                adminCategoryDTOs,
                adminPlatformDTOs,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }
}