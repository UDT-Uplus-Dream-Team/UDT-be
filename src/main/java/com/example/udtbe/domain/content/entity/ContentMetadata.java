package com.example.udtbe.domain.content.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.example.udtbe.global.entity.TimeBaseEntity;
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

    @Convert(converter = TagConverter.class)
    @Column(name = "category_tag")
    private List<String> categoryTag;

    @Convert(converter = TagConverter.class)
    @Column(name = "genre_tag")
    private List<String> genreTag;

    @Convert(converter = TagConverter.class)
    @Column(name = "platform_tag")
    private List<String> platformTag;

    @Convert(converter = TagConverter.class)
    @Column(name = "director_tag")
    private List<String> directorTag;

    @Convert(converter = TagConverter.class)
    @Column(name = "cast_tag")
    private List<String> castTag;


    @OneToOne(fetch = LAZY, optional = false)
    @JoinColumn(
            name = "content_id",
            nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Content content;

    @Builder(access = PRIVATE)
    private ContentMetadata(String title, String rating, boolean isDeleted,
            List<String> categoryTag,
            List<String> genreTag, List<String> platformTag, List<String> directorTag,
            List<String> castTag,
            Content content) {
        this.title = title;
        this.rating = rating;
        this.isDeleted = isDeleted;
        this.categoryTag = categoryTag;
        this.genreTag = genreTag;
        this.platformTag = platformTag;
        this.directorTag = directorTag;
        this.castTag = castTag;
        this.content = content;
    }

    public static ContentMetadata of(String title, String rating, List<String> categoryTag,
            List<String> genreTag, List<String> platformTag, List<String> directorTag,
            List<String> castTag,
            Content content) {
        return ContentMetadata.builder()
                .title(title)
                .rating(rating)
                .isDeleted(false)
                .categoryTag(categoryTag)
                .genreTag(genreTag)
                .platformTag(platformTag)
                .directorTag(directorTag)
                .castTag(castTag)
                .content(content)
                .build();
    }

    public void delete(boolean status) {
        this.isDeleted = status;
    }

    public void update(String title, String rating, List<String> categoryTag, List<String> genreTag,
            List<String> platformTag, List<String> directorTag, List<String> castTag) {
        this.title = title;
        this.rating = rating;
        this.categoryTag = categoryTag;
        this.genreTag = genreTag;
        this.platformTag = platformTag;
        this.directorTag = directorTag;
        this.castTag = castTag;
    }
}
