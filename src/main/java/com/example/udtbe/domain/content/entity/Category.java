package com.example.udtbe.domain.content.entity;

import static lombok.AccessLevel.PROTECTED;

import com.example.udtbe.domain.content.entity.enums.CategoryType;
import com.example.udtbe.global.entity.TimeBaseEntity;
import com.example.udtbe.global.util.CategoryTypeConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Category extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Convert(converter = CategoryTypeConverter.class)
    @Column(name = "category_type", nullable = false)
    private CategoryType categoryType;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Genre> genres = new ArrayList<>();

    private Category(CategoryType categoryType, boolean isDeleted, List<Genre> genres) {
        this.categoryType = categoryType;
        this.isDeleted = isDeleted;
        initGenres(genres);
    }

    public static Category of(CategoryType categoryType, boolean isDeleted, List<Genre> genres) {
        return new Category(categoryType, isDeleted, genres);
    }

    private void initGenres(List<Genre> genres) {
        genres.forEach(genre -> {
            this.genres.add(genre);
            genre.addCategory(this);
        });
    }
}
