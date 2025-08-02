package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.admin.dto.request.AdminDirectorsGetRequest;
import com.example.udtbe.domain.admin.dto.response.AdminDirectorsGetResponse;
import com.example.udtbe.global.dto.CursorPageResponse;

public interface DirectorQueryDSL {

    CursorPageResponse<AdminDirectorsGetResponse> getDirectors(AdminDirectorsGetRequest request);

}
