package com.example.udtbe.domain.content.entity;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.entity.TimeBaseEntity;
import com.example.udtbe.global.util.GenreTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feedback_statistics")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class FeedbackStatistics extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_statistics_id")
    private Long id;

    @Convert(converter = GenreTypeConverter.class)
    @Column(name = "genre_type", nullable = false)
    private GenreType genreType;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @Column(name = "dislike_count", nullable = false)
    private Long dislikeCount;

    @Column(name = "uninterested_count", nullable = false)
    private Long uninterestedCount;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Member member;

    @Builder(access = PRIVATE)
    private FeedbackStatistics(GenreType genreType, Long likeCount, Long dislikeCount,
            Long uninterestedCount, boolean isDeleted, Member member) {
        this.genreType = genreType;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.uninterestedCount = uninterestedCount;
        this.isDeleted = isDeleted;
        this.member = member;
    }

    public static FeedbackStatistics of(GenreType genreType, Long likeCount, Long dislikeCount,
            Long uninterestedCount, boolean isDeleted, Member member) {
        return FeedbackStatistics.builder()
                .genreType(genreType)
                .likeCount(likeCount)
                .dislikeCount(dislikeCount)
                .uninterestedCount(uninterestedCount)
                .isDeleted(isDeleted)
                .member(member)
                .build();
    }
}
