package com.example.udtbe.domain.content.entity;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "content_category")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ContentCategory extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_category_id")
    private Long id;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Category category;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "content_id",
            nullable = false,
            foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Content content;

    @Builder(access = PRIVATE)
    private ContentCategory(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public static ContentCategory of() {
        return ContentCategory.builder()
                .isDeleted(false)
                .build();
    }

    public void addContentAndCategory(Content content,Category category) {
        this.content = content;
        content.getContentCategories().add(this);
        this.category = category;
        category.getContentCategories().add(this);
    }

    public void delete(boolean status) {
        this.isDeleted=status;
    }

}
