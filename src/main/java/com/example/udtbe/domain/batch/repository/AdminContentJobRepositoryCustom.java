package com.example.udtbe.domain.batch.repository;

import com.example.udtbe.domain.admin.dto.response.AdminContentJobGetResponse;
import com.example.udtbe.domain.batch.entity.enums.BatchFilterType;
import com.example.udtbe.global.dto.CursorPageResponse;

public interface AdminContentJobRepositoryCustom {

    CursorPageResponse<AdminContentJobGetResponse> getJobsByCursor(
            String cursor, int size, BatchFilterType type);
}
