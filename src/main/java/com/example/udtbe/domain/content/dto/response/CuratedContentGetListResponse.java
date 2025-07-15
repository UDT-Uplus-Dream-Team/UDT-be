package com.example.udtbe.domain.content.dto.response;

import com.example.udtbe.domain.content.dto.common.CuratedContentDTO;
import java.util.List;

public record CuratedContentGetListResponse(
        List<CuratedContentDTO> contents,
        Long nextCursor,
        Boolean hasNext
) {

}
