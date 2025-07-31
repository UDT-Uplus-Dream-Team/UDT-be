package com.example.udtbe.domain.admin.dto.request;

import com.example.udtbe.domain.admin.dto.common.AdminCategoryDTO;
import com.example.udtbe.domain.admin.dto.common.AdminPlatformDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record AdminContentUpdateRequest(
        @NotBlank(message = "제목을 입력해주세요.")
        String title,

        String description,

        String posterUrl,

        String backdropUrl,

        String trailerUrl,

        LocalDateTime openDate,

        @NotNull(message = "러닝 타임을 입력해주세요. 영화가 아니면 0 입니다.")
        @Min(value = 0, message = "러닝 타임은 0 이상이어야 합니다.")
        int runningTime,

        @NotNull(message = "회차를 입력해주세요. 드라마,애니메이션,예능이 아니면 0입니다")
        @Min(value = 0, message = "회차는 0 이상이어야 합니다.")
        int episode,

        @NotBlank(message = "등급을 선택해주세요.")
        String rating,

        @NotEmpty(message = "분류를 하나 이상 선택해주세요.")
        List<AdminCategoryDTO> categories,

        List<String> countries,

        List<Long> directors,

        List<Long> casts,

        @NotEmpty(message = "플랫폼을 하나 이상 선택해주세요.")
        List<AdminPlatformDTO> platforms
) {

}
