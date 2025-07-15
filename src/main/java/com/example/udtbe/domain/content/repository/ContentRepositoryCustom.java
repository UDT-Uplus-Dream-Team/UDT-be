package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.admin.dto.common.ContentDTO;
import com.example.udtbe.domain.content.dto.request.ContentsGetRequest;
import com.example.udtbe.domain.content.dto.response.ContentsGetResponse;
import com.example.udtbe.global.dto.CursorPageResponse;

public interface ContentRepositoryCustom {

    CursorPageResponse<ContentDTO> findContentsAdminByCursor(Long cursor, int size);

    CursorPageResponse<ContentsGetResponse> getContents(ContentsGetRequest request);
}
