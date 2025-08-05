package com.example.udtbe.domain.admin.dto.response;

public record AdminContentDelJobGetDetailResponse(

        long contentId,

        String errorCode,

        String errorMessage,

        int retryCount,

        int skipCount

) {

}
