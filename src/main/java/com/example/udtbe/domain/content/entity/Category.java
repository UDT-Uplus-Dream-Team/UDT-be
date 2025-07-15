package com.example.udtbe.domain.content.entity;

import static lombok.AccessLevel.PRIVATE;
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
import lombok.Builder;
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

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<ContentCategory> contentCategories = new ArrayList<>();

    @Builder(access = PRIVATE)
    private Category(CategoryType categoryType, boolean isDeleted) {
        this.categoryType = categoryType;
        this.isDeleted = isDeleted;
    }

    public static Category of(CategoryType categoryType) {
        return Category.builder()
                .categoryType(categoryType)
                .isDeleted(false)
                .build();
    }


}
