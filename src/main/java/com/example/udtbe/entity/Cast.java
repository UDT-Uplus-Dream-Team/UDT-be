package com.example.udtbe.entity;

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
@Table(name = "cast")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Cast extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cast_id")
    private Long id;

    @Column(name = "cast_name", nullable = false)
    private String castName;

    @Column(name = "cast_image_url")
    private String castImageUrl;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Builder(access = PRIVATE)
    private Cast(String castName, String castImageUrl, boolean isDeleted) {
        this.castName = castName;
        this.castImageUrl = castImageUrl;
        this.isDeleted = isDeleted;
    }

    public static Cast of(String castName, String castImageUrl, boolean isDeleted) {
        return Cast.builder()
                .castName(castName)
                .castImageUrl(castImageUrl)
                .isDeleted(isDeleted)
                .build();
    }
}
