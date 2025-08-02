package com.example.udtbe.domain.admin.dto;

import com.example.udtbe.domain.admin.dto.response.AdminMemberListGetResponse;
import com.example.udtbe.domain.content.entity.FeedbackStatistics;
import com.example.udtbe.domain.member.entity.Member;
import java.util.List;

public class AdminMemberMapper {

    private AdminMemberMapper() {
    }

    public static AdminMemberListGetResponse toListDto(Member member,
            List<FeedbackStatistics> stats) {

        List<FeedbackStatistics> statList = stats != null ? stats : List.of();

        int likeSum = statList.stream().mapToInt(FeedbackStatistics::getLikeCount).sum();
        int dislikeSum = statList.stream().mapToInt(FeedbackStatistics::getDislikeCount).sum();
        int uninterestedSum = statList.stream().mapToInt(FeedbackStatistics::getUninterestedCount)
                .sum();

        return new AdminMemberListGetResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getRole(),
                member.getProfileImageUrl(),
                member.getLastLoginAt(),
                likeSum,
                dislikeSum,
                uninterestedSum
        );
    }
}
