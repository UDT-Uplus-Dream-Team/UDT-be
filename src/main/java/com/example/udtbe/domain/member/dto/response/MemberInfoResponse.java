package com.example.udtbe.domain.member.dto.response;

import java.util.List;

public record MemberInfoResponse(
        String name,
        String email,
        List<String> platforms,
        List<String> genres,
        String profileImageUrl
) {

}
