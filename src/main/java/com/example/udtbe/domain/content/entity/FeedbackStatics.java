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
@Table(name = "feedback_statics")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class FeedbackStatics extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_statics_id")
    private Long id;

    @Convert(converter = GenreTypeConverter.class)
    @Column(name = "genre_type", nullable = false)
    private GenreType genreType;

    @Column(name = "like", nullable = false)
    private Long like;

    @Column(name = "dislike", nullable = false)
    private Long dislike;

    @Column(name = "uninterested", nullable = false)
    private Long uninterested;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Member member;

    @Builder(access = PRIVATE)
    private FeedbackStatics(GenreType genreType, Long like, Long dislike,
            Long uninterested, boolean isDeleted, Member member) {
        this.genreType = genreType;
        this.like = like;
        this.dislike = dislike;
        this.uninterested = uninterested;
        this.isDeleted = isDeleted;
        this.member = member;
    }

    public static FeedbackStatics of(GenreType genreType, Long like, Long dislike,
            Long uninterested, boolean isDeleted, Member member) {
        return FeedbackStatics.builder()
                .genreType(genreType)
                .like(like)
                .dislike(dislike)
                .uninterested(uninterested)
                .isDeleted(isDeleted)
                .member(member)
                .build();
    }
}
