package com.example.udtbe.domain.content.entity;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.example.udtbe.domain.content.entity.enums.CategoryType;
import com.example.udtbe.global.entity.TimeBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Enumerated(value = STRING)
    @Column(name = "category_type", nullable = false)
    private CategoryType categoryType;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Builder(access = PRIVATE)
    private Category(CategoryType categoryType, boolean isDeleted) {
        this.categoryType = categoryType;
        this.isDeleted = isDeleted;
    }

    public static Category of(CategoryType categoryType, boolean isDeleted) {
        return Category.builder()
                .categoryType(categoryType)
                .isDeleted(isDeleted)
                .build();
    }
}
