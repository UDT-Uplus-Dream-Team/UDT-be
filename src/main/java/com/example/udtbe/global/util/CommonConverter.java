package com.example.udtbe.global.util;

import static com.example.udtbe.global.exception.CommonErrorCode.INVALID_PARAMETER;

import com.example.udtbe.global.exception.RestApiException;
import java.util.List;
import java.util.Objects;

public abstract class CommonConverter {

    protected void validateNotNull(Object arg) {
        if (Objects.isNull(arg)) {
            throw new RestApiException(INVALID_PARAMETER);
        }
    }

    protected void validateList(List<String> args) {
        if (Objects.isNull(args) || args.isEmpty()) {
            throw new RestApiException(INVALID_PARAMETER);
        }
    }

}
