package com.example.udtbe.domain.content.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

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
@Table(name = "content_metadata")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ContentMetadata extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_metadata_id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "rating")
    private String rating;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "genre_tag")
    private String genreTag;

    @Column(name = "platform_tag")
    private String platformTag;

    @Column(name = "director_tag")
    private String directorTag;

    @OneToOne(fetch = LAZY, optional = false)
    @JoinColumn(
            name = "content_id",
            nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Content content;

    @Builder(access = PRIVATE)
    private ContentMetadata(String title, String rating, boolean isDeleted,
            String genreTag, String platformTag, String directorTag,
            Content content) {
        this.title = title;
        this.rating = rating;
        this.isDeleted = isDeleted;
        this.genreTag = genreTag;
        this.platformTag = platformTag;
        this.directorTag = directorTag;
        this.content = content;
    }

    public static ContentMetadata of(String title, String rating, boolean isDeleted,
            String genreTag, String platformTag, String directorTag,
            Content content) {
        return ContentMetadata.builder()
                .title(title)
                .rating(rating)
                .isDeleted(isDeleted)
                .genreTag(genreTag)
                .platformTag(platformTag)
                .directorTag(directorTag)
                .content(content)
                .build();
    }
}
