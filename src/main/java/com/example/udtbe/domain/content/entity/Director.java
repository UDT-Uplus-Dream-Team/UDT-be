package com.example.udtbe.domain.content.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.example.udtbe.global.entity.TimeBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "director")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Director extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "director_id")
    private Long id;

    @Column(name = "director_name", nullable = false)
    private String directorName;

    @Column(name = "director_image_url")
    private String directorImageUrl;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Builder(access = PRIVATE)
    private Director(String directorName, boolean isDeleted, String directorImageUrl) {
        this.directorName = directorName;
        this.isDeleted = isDeleted;
        this.directorImageUrl = directorImageUrl;
    }

    public static Director of(String directorName) {
        return Director.builder()
                .directorName(directorName)
                .isDeleted(false)
                .build();
    }
}
