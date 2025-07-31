package com.example.udtbe.domain.admin.dto.response;

import com.example.udtbe.domain.admin.dto.common.AdminMemberGenreFeedbackDTO;
import java.time.LocalDateTime;
import java.util.List;

public record AdminMemberFeedbackGetResponse(
        Long id,
        String name,
        String email,
        LocalDateTime lastLoginAt,
        long likeCount,
        long dislikeCount,
        long uninterestedCount,
        List<AdminMemberGenreFeedbackDTO> genres
) {

}
