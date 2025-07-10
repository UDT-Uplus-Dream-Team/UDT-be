package com.example.udtbe.domain.content.entity;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

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
@Table(name = "content_genre")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ContentGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_genre_id")
    private Long id;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "genre_id",
            nullable = false,
            foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Genre genre;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "content_id",
            nullable = false,
            foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Content content;

    @Builder(access = PRIVATE)
    private ContentGenre(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public static ContentGenre of() {
        return ContentGenre.builder()
                .isDeleted(false)
                .build();
    }

    public void addContentAndGenre(Content content, Genre genre) {
        this.content = content;
        content.getContentGenres().add(this);
        this.genre = genre;
        genre.getContentGenres().add(this);
    }

    public void delete(boolean status) {
        this.isDeleted=status;
    }
}
