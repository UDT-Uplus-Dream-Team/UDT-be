package com.example.udtbe.domain.admin.dto.request;

import com.example.udtbe.domain.admin.dto.common.CastDTO;
import com.example.udtbe.domain.admin.dto.common.CategoryDTO;
import com.example.udtbe.domain.admin.dto.common.PlatformDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record ContentRegisterRequest(
        @NotBlank(message = "제목을 입력해주세요.")
        String title,

        String description,

        String posterUrl,

        String backdropUrl,

        String trailerUrl,

        LocalDateTime openDate,

        @NotNull(message = "상영 시간을 입력해주세요. 영화가 아니면 0 입니다.")
        @Min(value = 0, message = "상영 시간은 0 이상이어야 합니다.")
        int runningTime,

        @NotNull(message = "회차를 입력해주세요. 영화이면 0입니다.")
        @Min(value = 0, message = "회차는 0 이상이어야 합니다.")
        int episode,

        @NotBlank(message = "등급을 선택해주세요.")
        String rating,

        @NotEmpty(message = "카테고리를 하나 이상 선택해주세요.")
        @Valid
        List<CategoryDTO> categories,

        List<String> countries,

        List<String> directors,

        List<CastDTO> casts,

        @NotEmpty(message = "플랫폼을 하나 이상 선택해주세요.")
        @Valid
        List<PlatformDTO> platforms
) {
}
