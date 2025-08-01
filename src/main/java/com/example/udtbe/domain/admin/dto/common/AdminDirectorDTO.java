package com.example.udtbe.domain.admin.dto.common;

import jakarta.validation.constraints.NotBlank;

public record AdminDirectorDTO(
        @NotBlank(message = "감독 이름은 필수입니다.")
        String directorName,
        @NotBlank(message = "감독 사진 주소은 필수입니다.")
        String directorImageUrl
) {

}
