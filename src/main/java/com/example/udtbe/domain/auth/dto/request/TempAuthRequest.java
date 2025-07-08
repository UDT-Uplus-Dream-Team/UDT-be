package com.example.udtbe.domain.auth.dto.request;

import jakarta.validation.constraints.Email;

public record TempAuthRequest(
        @Email
        String email
) {

}
