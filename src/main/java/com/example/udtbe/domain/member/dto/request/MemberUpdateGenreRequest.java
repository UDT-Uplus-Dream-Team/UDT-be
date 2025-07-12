package com.example.udtbe.domain.member.dto.request;

import java.util.List;

public record MemberUpdateGenreRequest(
        List<String> genres
) {

}
