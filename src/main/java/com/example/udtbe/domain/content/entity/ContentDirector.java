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
@Table(name = "content_director")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ContentDirector extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_director_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "director_id",
            nullable = false,
            foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Director director;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "content_id",
            nullable = false,
            foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Content content;

    private ContentDirector(Content content, Director director) {
        addContentAndDirector(content, director);
    }

    public static ContentDirector of(Content content, Director director) {
        return new ContentDirector(content, director);
    }

    private void addContentAndDirector(Content content, Director director) {
        this.content = content;
        content.getContentDirectors().add(this);
        this.director = director;
    }
}
