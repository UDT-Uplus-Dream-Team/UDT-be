package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.admin.dto.common.ContentDTO;
import com.example.udtbe.global.dto.CursorPageResponse;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepositoryCustom {

    CursorPageResponse<ContentDTO> findContentsAdminByCursor(Long cursor, int size);
}
