package com.example.udtbe.domain.content.entity;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.entity.TimeBaseEntity;
import jakarta.persistence.Column;
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
@Table(name = "curated_content")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class CuratedContent extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curated_content_id")
    private Long id;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "content_id",
            nullable = false,
            foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Content content;

    @Builder(access = PRIVATE)
    private CuratedContent(boolean isDeleted, Member member, Content content) {
        this.isDeleted = isDeleted;
        this.member = member;
        this.content = content;
    }

    public static CuratedContent of(boolean isDeleted, Member member, Content content) {
        return CuratedContent.builder()
                .isDeleted(isDeleted)
                .member(member)
                .content(content)
                .build();
    }
}
