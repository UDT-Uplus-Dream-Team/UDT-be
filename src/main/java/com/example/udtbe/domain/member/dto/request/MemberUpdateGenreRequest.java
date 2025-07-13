package com.example.udtbe.domain.member.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record MemberUpdateGenreRequest(

        @NotNull(message = "장르 선택은 필수 입니다.")
        @Size(min = 1, max = 3, message = "장르 수정은 최소 1개 이상 최대 3개 이하입니다.")
        List<String> genres
) {

}
