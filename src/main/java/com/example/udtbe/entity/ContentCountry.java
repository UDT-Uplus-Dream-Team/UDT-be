package com.example.udtbe.entity;

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
@Table(name = "content_country")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ContentCountry extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_country_id")
    private Long id;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "country_id",
            nullable = false,
            foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Country country;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "content_id",
            nullable = false,
            foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Content content;

    @Builder(access = PRIVATE)
    private ContentCountry(boolean isDeleted, Country country, Content content) {
        this.isDeleted = isDeleted;
        this.country = country;
        this.content = content;
    }

    public static ContentCountry of(boolean isDeleted, Country country, Content content) {
        return ContentCountry.builder()
                .isDeleted(isDeleted)
                .country(country)
                .content(content)
                .build();
    }
}
