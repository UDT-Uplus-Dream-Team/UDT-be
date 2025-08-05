package com.example.udtbe.domain.admin.dto;

import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.admin.dto.response.AdminMembersGetResponse;
import com.example.udtbe.domain.content.entity.FeedbackStatistics;
import com.example.udtbe.domain.member.entity.Member;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class AdminMemberMapper {

    public static AdminMembersGetResponse getMembers(Member member,
            List<FeedbackStatistics> stats) {

        List<FeedbackStatistics> statList = stats != null ? stats : List.of();

        int likeSum = statList.stream().mapToInt(FeedbackStatistics::getLikeCount).sum();
        int dislikeSum = statList.stream().mapToInt(FeedbackStatistics::getDislikeCount).sum();
        int uninterestedSum = statList.stream().mapToInt(FeedbackStatistics::getUninterestedCount)
                .sum();

        return new AdminMembersGetResponse(
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
