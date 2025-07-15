package com.example.udtbe.domain.content.entity;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.global.entity.TimeBaseEntity;
import com.example.udtbe.global.util.GenreTypeConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "genre")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Genre extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    private Long id;

    @Convert(converter = GenreTypeConverter.class)
    @Column(name = "genre_type", nullable = false)
    private GenreType genreType;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Category category;

    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL)
    private List<ContentGenre> contentGenres = new ArrayList<>();

    @Builder(access = PRIVATE)
    private Genre(GenreType genreType, boolean isDeleted, Category category) {
        this.genreType = genreType;
        this.isDeleted = isDeleted;
        this.category = category;
    }

    public static Genre of(GenreType genreType, Category category) {
        return Genre.builder()
                .genreType(genreType)
                .category(category)
                .isDeleted(false)
                .build();
    }

}
