package com.example.udtbe.domain.admin.dto.request;

import com.example.udtbe.domain.admin.dto.common.AdminDirectorDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record AdminDirectorsRegisterRequest(
        @NotNull(message = "감독 정보는 필수입니다.")
        @Size(max = 30, message = "감독은 최대 30명까지 등록할 수 있습니다.")
        @Valid
        @JsonProperty("directors")
        List<AdminDirectorDTO> adminDirectorDTOS
) {

}
