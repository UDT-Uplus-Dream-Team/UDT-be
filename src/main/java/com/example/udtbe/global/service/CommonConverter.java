package com.example.udtbe.global.service;

import static com.example.udtbe.global.exception.CommonErrorCode.*;

import java.util.Objects;


import com.example.udtbe.global.exception.RestApiException;

public abstract class CommonConverter {
	protected void validateNotNull(Object arg) {
		if(Objects.isNull(arg)) {
			throw new RestApiException(INVALID_PARAMETER);
		}
	}

}
