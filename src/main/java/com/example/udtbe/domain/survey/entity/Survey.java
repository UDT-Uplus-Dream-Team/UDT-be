package com.example.udtbe.domain.survey.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.entity.TimeBaseEntity;
import com.example.udtbe.global.util.OptionalTagConverter;
import com.example.udtbe.global.util.TagConverter;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
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

    @Convert(converter = TagConverter.class)
    @Column(name = "platform_tag", nullable = false)
    private List<String> platformTag;

    @Convert(converter = TagConverter.class)
    @Column(name = "genre_tag", nullable = false)
    private List<String> genreTag;

    @Convert(converter = OptionalTagConverter.class)
    @Column(name = "content_tag")
    private List<String> contentTag;

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
    private Survey(List<String> platformTag, List<String> genreTag, List<String> contentTag,
            boolean isDeleted, Member member) {
        this.platformTag = platformTag;
        this.genreTag = genreTag;
        this.contentTag = contentTag;
        this.isDeleted = isDeleted;
        this.member = member;
    }

    public static Survey of(List<String> platformTag, List<String> genreTag,
            List<String> contentTag, boolean isDeleted, Member member) {
        return Survey.builder()
                .platformTag(platformTag)
                .genreTag(genreTag)
                .contentTag(contentTag)
                .isDeleted(isDeleted)
                .member(member)
                .build();
    }
}
