package com.example.udtbe.domain.admin.dto.common;

import jakarta.validation.constraints.NotBlank;

public record AdminCastDTO(
        @NotBlank(message = "출연진 이름은 필수입니다.")
        String castName,
        @NotBlank(message = "출연진 사진 주소은 필수입니다.")
        String castImageUrl
) {

}
