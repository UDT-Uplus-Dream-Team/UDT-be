package com.example.udtbe.domain.admin.dto.response;

import com.example.udtbe.domain.admin.dto.common.AdminMemberGenreFeedbackDTO;
import java.time.LocalDateTime;
import java.util.List;

public record AdminMemberInfoGetResponse(
        Long id,
        String name,
        String email,
        LocalDateTime lastLoginAt,
        long totalLikeCount,
        long totalDislikeCount,
        long totalUninterestedCount,
        List<AdminMemberGenreFeedbackDTO> genres
) {

}
