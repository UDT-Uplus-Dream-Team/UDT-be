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
@Table(name = "content_country")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ContentCountry extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_country_id")
    private Long id;

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

    private ContentCountry(Content content, Country country) {
        addContentAndCountry(content, country);
    }

    public static ContentCountry of(Content content, Country country) {
        return new ContentCountry(content, country);
    }

    private void addContentAndCountry(Content content, Country country) {
        this.content = content;
        content.getContentCountries().add(this);
        this.country = country;
    }
}
