package com.example.udtbe.domain.admin.dto;

import com.example.udtbe.domain.admin.dto.common.CastDTO;
import com.example.udtbe.domain.admin.dto.common.CategoryDTO;
import com.example.udtbe.domain.admin.dto.common.PlatformDTO;
import com.example.udtbe.domain.admin.dto.request.ContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.response.ContentGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.ContentRegisterResponse;
import com.example.udtbe.domain.content.entity.Content;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ContentMapper {

    public static Content toContentEntity(ContentRegisterRequest contentRegisterRequest) {
        return Content.of(
                contentRegisterRequest.title(),
                contentRegisterRequest.description(),
                contentRegisterRequest.posterUrl(),
                contentRegisterRequest.backdropUrl(),
                contentRegisterRequest.trailerUrl(),
                contentRegisterRequest.openDate(),
                contentRegisterRequest.runningTime(),
                contentRegisterRequest.episode(),
                contentRegisterRequest.rating()
        );
    }

    public static ContentRegisterResponse toContentRegisterResponse(Content content) {
        return new ContentRegisterResponse(content.getId());
    }

    public static ContentGetDetailResponse toContentGetResponse(Content content,
            List<CategoryDTO> categories, List<CastDTO> casts, List<String> directors,
            List<String> countries, List<PlatformDTO> platforms) {

        return new ContentGetDetailResponse(
                content.getTitle(),
                content.getDescription(),
                content.getPosterUrl(),
                content.getBackdropUrl(),
                content.getTrailerUrl(),
                content.getOpenDate(),
                content.getRunningTime(),
                content.getEpisode(),
                content.getRating(),
                categories,
                countries,
                directors,
                casts,
                platforms
        );
    }

}
