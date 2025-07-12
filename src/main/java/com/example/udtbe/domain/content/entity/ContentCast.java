package com.example.udtbe.domain.content.entity;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "content_cast")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ContentCast extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_cast_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cast_id",
            nullable = false,
            foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Cast cast;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "content_id",
            nullable = false,
            foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Content content;

    private ContentCast(Content content, Cast cast) {
        addContentAndCast(content, cast);
    }

    public static ContentCast of(Content content, Cast cast) {
        return new ContentCast(content, cast);
    }

    private void addContentAndCast(Content content, Cast cast) {
        this.content = content;
        content.getContentCasts().add(this);
        this.cast = cast;
    }
}
