package com.example.udtbe.domain.admin.dto;

import com.example.udtbe.domain.admin.dto.common.AdminCastDetailsDTO;
import com.example.udtbe.domain.admin.dto.common.AdminCategoryDTO;
import com.example.udtbe.domain.admin.dto.common.AdminDirectorDetailsDTO;
import com.example.udtbe.domain.admin.dto.common.AdminMemberGenreFeedbackDTO;
import com.example.udtbe.domain.admin.dto.common.AdminPlatformDTO;
import com.example.udtbe.domain.admin.dto.request.AdminContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentUpdateResponse;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.FeedbackStatistics;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AdminContentMapper {

    public static Content toContentEntity(AdminContentRegisterRequest adminContentRegisterRequest) {
        return Content.of(
                adminContentRegisterRequest.title(),
                adminContentRegisterRequest.description(),
                adminContentRegisterRequest.posterUrl(),
                adminContentRegisterRequest.backdropUrl(),
                adminContentRegisterRequest.trailerUrl(),
                adminContentRegisterRequest.openDate(),
                adminContentRegisterRequest.runningTime(),
                adminContentRegisterRequest.episode(),
                adminContentRegisterRequest.rating()
        );
    }

    public static AdminContentRegisterResponse toContentRegisterResponse(Content content) {
        return new AdminContentRegisterResponse(content.getId());
    }

    public static AdminContentUpdateResponse toContentUpdateResponse(Content content) {
        return new AdminContentUpdateResponse(content.getId());
    }

    public static AdminContentGetDetailResponse toContentGetResponse(Content content,
            List<AdminCategoryDTO> categories, List<AdminCastDetailsDTO> castDetailsDTOS,
            List<AdminDirectorDetailsDTO> directorDetailsDTOS, List<String> countries,
            List<AdminPlatformDTO> platforms) {

        return new AdminContentGetDetailResponse(
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
                directorDetailsDTOS,
                castDetailsDTOS,
                platforms
        );
    }

    public static List<AdminMemberGenreFeedbackDTO> toGenreFeedbackDtoList(
            List<FeedbackStatistics> stats) {

        return stats.stream()
                .map(fs -> new AdminMemberGenreFeedbackDTO(
                        fs.getGenreType(),
                        fs.getLikeCount(),
                        fs.getDislikeCount(),
                        fs.getUninterestedCount()))
                .toList();
    }
}
