package com.example.udtbe.domain.survey.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.entity.TimeBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "survey")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Survey extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_id")
    private Long id;

    @Column(name = "platform_tag", nullable = false)
    private String platformTag;

    @Column(name = "genre_tag", nullable = false)
    private String genreTag;

    @Column(name = "content_tag")
    private String contentTag;

    @Column(name = "is_age_rating_limit", nullable = false)
    private boolean isAgeRatingLimit;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @OneToOne(fetch = LAZY, optional = false)
    @JoinColumn(
            name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Member member;

    @Builder(access = PRIVATE)
    private Survey(String platformTag, String genreTag, String contentTag,
            boolean isAgeRatingLimit, boolean isDeleted, Member member) {
        this.platformTag = platformTag;
        this.genreTag = genreTag;
        this.contentTag = contentTag;
        this.isAgeRatingLimit = isAgeRatingLimit;
        this.isDeleted = isDeleted;
        this.member = member;
    }

    public static Survey of(String platformTag, String genreTag, String contentTag,
            boolean isAgeRatingLimit, boolean isDeleted, Member member) {
        return Survey.builder()
                .platformTag(platformTag)
                .genreTag(genreTag)
                .contentTag(contentTag)
                .isAgeRatingLimit(isAgeRatingLimit)
                .isDeleted(isDeleted)
                .member(member)
                .build();
    }
}
