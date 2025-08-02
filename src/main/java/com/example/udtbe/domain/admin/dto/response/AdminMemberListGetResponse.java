package com.example.udtbe.domain.admin.dto.response;

import com.example.udtbe.domain.member.entity.enums.Role;
import java.time.LocalDateTime;

public record AdminMemberListGetResponse(
        Long id,
        String name,
        String email,
        Role userRole,
        String profileImageUrl,
        LocalDateTime lastLoginAt,
        int totalLikeCount,
        int totalDislikeCount,
        int totalUninterestedCount
) {

}
