package com.example.udtbe.domain.member.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record MemberUpdatePlatformRequest(

        @NotNull(message = "플랫폼 선택은 필수 입니다.")
        @Size(min = 1, max = 7, message = "OTT 플랫폼은 최소 1개 이상 최대 7개 이하입니다.")
        List<String> platforms
) {

}
