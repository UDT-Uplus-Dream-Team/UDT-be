package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.admin.dto.response.AdminContentGetResponse;
import com.example.udtbe.domain.content.dto.request.ContentsGetRequest;
import com.example.udtbe.domain.content.dto.response.ContentsGetResponse;
import com.example.udtbe.global.dto.CursorPageResponse;

public interface ContentRepositoryCustom {

    CursorPageResponse<AdminContentGetResponse> getsAdminContentsByCursor(Long cursor, int size,
            String categoryType);

    CursorPageResponse<ContentsGetResponse> getContents(ContentsGetRequest request);
}
