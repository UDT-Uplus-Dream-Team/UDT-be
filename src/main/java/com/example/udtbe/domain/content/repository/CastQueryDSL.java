package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.admin.dto.request.AdminCastsGetRequest;
import com.example.udtbe.domain.admin.dto.response.AdminCastsGetResponse;
import com.example.udtbe.global.dto.CursorPageResponse;

public interface CastQueryDSL {

    CursorPageResponse<AdminCastsGetResponse> getCasts(AdminCastsGetRequest request);
}
